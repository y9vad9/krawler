package krawler.server.auth.application

import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.auth.domain.OwnershipChallenge
import krawler.server.auth.domain.OwnershipChallengeResult

/**
 * Completes an authentication session by validating the ownership challenge
 * and issuing access and refresh tokens on success.
 *
 * Ensures:
 * - Session exists and is not expired.
 * - Attempts are within allowed limits.
 * - Ownership challenge is validated against the last battle.
 *
 * Returns a [Result] indicating success, domain-level failure, or unexpected repository failure.
 */
class CompleteAuthenticationUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val sessionsRepository: AuthenticationSessionsRepository,
    private val authTokenGenerator: AuthenticationTokenGenerator,
    private val timeProvider: TimeProvider,
    private val battleRepository: BattleRepository,
    private val hasher: Hasher,
    private val config: AuthenticationConfig,
) {
    private companion object {
        const val PLAYER_TAG_LOGGING_KEY: String = "challenged_player_tag"
    }

    /**
     * Completes authentication for the given [token].
     */
    suspend fun execute(token: AuthenticationSessionToken, logger: Logger): Result {
        val challenge = fetchChallenge(token, logger) ?: return Result.SessionNotFound

        val currentTime = timeProvider.provide()
        if (challenge.isExpired(currentTime)) return Result.SessionExpired
        if (challenge.isAttemptsExceeded()) return Result.AttemptsExceeded

        val lastBattle = fetchLastBattle(challenge, logger) ?: return Result.Failure

        return when (challenge.attempt(currentTime, lastBattle)) {
            OwnershipChallengeResult.Success -> handleSuccess(challenge, currentTime, logger)
            OwnershipChallengeResult.BattleBeforeTask,
            OwnershipChallengeResult.InvalidBrawler,
            OwnershipChallengeResult.InvalidEventType,
            OwnershipChallengeResult.InvalidBotsAmount -> handleAttemptFailure(challenge, token, logger)
            OwnershipChallengeResult.TaskExpired,
            OwnershipChallengeResult.AttemptsExceeded -> error("Should not reach this state; validated beforehand")
        }
    }

    private suspend fun fetchChallenge(token: AuthenticationSessionToken, logger: Logger): OwnershipChallenge? {
        return sessionsRepository.getChallenge(token)
            .getOrElse { throwable ->
                logger.withField("token", hasher.sha512(token.string))
                    .withThrowable(throwable)
                    .debug("Failed to fetch session.")
                null
            }
    }

    private suspend fun fetchLastBattle(challenge: OwnershipChallenge, logger: Logger) =
        battleRepository.getLastBattle(challenge.playerTag)
            .getOrElse { throwable ->
                logger.withField(PLAYER_TAG_LOGGING_KEY, challenge.playerTag.stringWithTagPrefix)
                    .withThrowable(throwable)
                    .warning("Failed to fetch last battle.")
                null
            }

    private suspend fun handleAttemptFailure(
        challenge: OwnershipChallenge,
        token: AuthenticationSessionToken,
        logger: Logger
    ): Result {
        sessionsRepository.addAttempt(token)
            .getOrElse { throwable ->
                logger.withField(PLAYER_TAG_LOGGING_KEY, challenge.playerTag.stringWithTagPrefix)
                    .withThrowable(throwable)
                    .warning("Failed to increment session attempt count.")
            }
        return Result.AttemptFailed
    }

    private suspend fun handleSuccess(
        challenge: OwnershipChallenge,
        currentTime: kotlin.time.Instant,
        logger: Logger
    ): Result {
        val authentication = Authentication(
            accessToken = authTokenGenerator.generateAccessToken(
                tag = challenge.playerTag,
                expiresAt = currentTime + config.accessTokenDuration
            ),
            refreshToken = authTokenGenerator.generateRefreshToken(),
            issueTime = currentTime,
            expiresAt = currentTime + config.refreshTokenDuration,
            playerTag = challenge.playerTag
        )

        authenticationRepository.issue(authentication)
            .getOrElse { throwable ->
                logger.withField(PLAYER_TAG_LOGGING_KEY, challenge.playerTag.stringWithTagPrefix)
                    .withThrowable(throwable)
                    .warning("Failed to persist authentication session.")
                return Result.Failure
            }

        return Result.Success(authentication)
    }

    /** Outcome of completing an authentication session. */
    sealed interface Result {
        /** Player has exceeded allowed attempts. */
        data object AttemptsExceeded : Result

        /** No active session found for the given token. */
        data object SessionNotFound : Result

        /** Session has expired. */
        data object SessionExpired : Result

        /** Ownership challenge validation failed. */
        data object AttemptFailed : Result

        /** Authentication successfully completed. */
        data class Success(val authentication: Authentication) : Result

        /** Unexpected repository or infrastructure failure. */
        data object Failure : Result
    }
}

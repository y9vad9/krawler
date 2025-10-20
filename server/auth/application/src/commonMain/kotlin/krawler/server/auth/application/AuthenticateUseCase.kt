package krawler.server.auth.application

import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.auth.domain.ChallengeId
import krawler.server.auth.domain.ChallengedPlayerTag
import krawler.server.auth.domain.OwnershipChallenge
import krawler.server.auth.domain.OwnershipChallengeAttempts
import krawler.server.auth.domain.OwnershipChallengeTimeFrame
import kotlin.uuid.Uuid

/**
 * Handles the initial phase of player authentication.
 *
 * The use case validates the player’s existence, ensures they haven’t exceeded
 * the allowed number of failed or active authentication sessions, and then issues
 * a new authentication session with an ownership challenge.
 *
 * **Process:**
 * 1. Checks if a player with the given [tag] exists.
 * 2. Verifies the number of active or failed sessions within a configured time window.
 * 3. Generates a new session token and ownership challenge.
 * 4. Persists the issued session via [sessionsRepository].
 *
 * Returns [Result.Success] with the generated session token and challenge on success,
 * or an appropriate failure result otherwise.
 *
 * @param playerRepository Repository used to verify player existence.
 * @param sessionsRepository Repository managing authentication sessions.
 * @param timeProvider Provides the current time for session and challenge lifetimes.
 * @param sessionTokenGenerator Generates unique authentication session tokens.
 * @param ownershipTaskGenerator Produces new ownership challenges for verification.
 * @param uuidProvider Provides new securely randomized [Uuid] for challenges.
 * @param config Authentication-related thresholds and durations.
 */
class AuthenticateUseCase(
    private val playerRepository: PlayerRepository,
    private val sessionsRepository: AuthenticationSessionsRepository,
    private val timeProvider: TimeProvider,
    private val sessionTokenGenerator: SessionTokenGenerator,
    private val ownershipTaskGenerator: OwnershipTaskGenerator,
    private val uuidProvider: UuidProvider,
    private val config: AuthenticationConfig,
) {
    private companion object {
        const val PLAYER_TAG_LOGGER_KEY: String = "player_tag"
    }

    /**
     * Executes the authentication flow for the given player [tag].
     *
     * @param tag The player tag for which to authenticate.
     * @param logger Logger instance used to record failures during the execution.
     *
     * @return [Result.Success] if a new session and challenge were successfully issued,
     *         [Result.PlayerDoesNotExist] if the player doesn’t exist,
     *         [Result.TooManyAttempts] if recent failed or active sessions exceed threshold,
     *         [Result.Failure] if an unexpected repository or infrastructure error occurs.
     */
    suspend fun execute(tag: ChallengedPlayerTag, logger: Logger): Result {
        val exists = playerRepository.exists(tag).getOrElse { throwable ->
            logger.withField(PLAYER_TAG_LOGGER_KEY, tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Failed to check existence of a player")

            return Result.Failure
        }
        if (!exists) return Result.PlayerDoesNotExist

        val currentTime = timeProvider.provide()
        val failedOrActiveSessionsCount = sessionsRepository.getSessionsCount(
            timeline = currentTime - config.activeOrFailedSessionsPeriod..currentTime,
            includeSuccessful = false,
            includeFailed = true,
        ).getOrElse { throwable ->
            logger.withField(PLAYER_TAG_LOGGER_KEY, tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .error("Failed to obtain count of failed & active sessions")
            return Result.Failure
        }

        if (failedOrActiveSessionsCount >= config.activeOrFailedSessionsThreshold)
            return Result.TooManyAttempts

        val token = sessionTokenGenerator.generate()

        val challenge = OwnershipChallenge(
            id = ChallengeId(uuidProvider.provide()),
            playerTag = tag,
            task = ownershipTaskGenerator.generate(),
            timeframe = OwnershipChallengeTimeFrame(currentTime, currentTime + config.sessionDuration),
            attempts = OwnershipChallengeAttempts.ZERO,
            maxAttempts = OwnershipChallengeAttempts.createOrThrow(config.attemptsPerSession),
        )

        sessionsRepository.issueSession(
            tag = tag,
            token = token,
            currentTime = currentTime,
            duration = config.sessionDuration,
            challenge = challenge,
        ).getOrElse { throwable ->
            logger.withField(PLAYER_TAG_LOGGER_KEY, tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .error("Failed to persist authentication session.")
            return Result.Failure
        }

        return Result.Success(token, challenge)
    }

    /** Represents possible outcomes of [execute]. */
    sealed interface Result {
        /** Successful session and challenge creation. */
        data class Success(val token: AuthenticationSessionToken, val challenge: OwnershipChallenge) : Result
        /** No player with the given tag exists. */
        data object PlayerDoesNotExist : Result
        /** Too many recent failed or active authentication attempts. */
        data object TooManyAttempts : Result
        /** Unexpected infrastructure or repository failure. */
        data object Failure : Result
    }
}

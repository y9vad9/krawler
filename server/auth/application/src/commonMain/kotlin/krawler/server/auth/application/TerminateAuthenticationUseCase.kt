package krawler.server.auth.application

import krawler.logger.Logger
import krawler.logger.withThrowable

/**
 * Handles termination of authentication sessions identified by a [RefreshToken].
 *
 * Uses [authenticationRepository] to revoke persisted authentication state.
 */
class TerminateAuthenticationUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val hasher: Hasher,
) {

    /**
     * Attempts to terminate the session associated with the given [refreshToken].
     *
     * @param refreshToken Token identifying the session to terminate.
     * @param logger Used for structured logging of termination results or errors.
     * @return [Result] indicating the outcome of the termination attempt.
     */
    suspend fun execute(refreshToken: RefreshToken, logger: Logger): Result {
        val isTerminated = authenticationRepository.terminate(refreshToken)
            .getOrElse { throwable ->
                logger
                    .withField("refresh_token_hash", hasher.sha512(refreshToken.string))
                    .withThrowable(throwable)
                    .warning("Failed to terminate authentication")
                return Result.Failure
            }

        return if (isTerminated) Result.Success else Result.NotFound
    }

    /**
     * Represents the outcome of a session termination attempt.
     */
    sealed interface Result {
        /** Session was successfully terminated. */
        data object Success : Result

        /** No session was found for the given [RefreshToken]. */
        data object NotFound : Result

        /** Termination failed due to repository or infrastructure error. */
        data object Failure : Result
    }
}

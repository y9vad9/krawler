package krawler.server.auth.application

/**
 * Repository interface for managing authentication sessions and tokens.
 *
 * Implementations are responsible for persisting and revoking authentication
 * sessions, including access and refresh tokens.
 */
interface AuthenticationRepository {

    /**
     * Persists a new authentication session, including its tokens and associated data.
     *
     * @param authentication The authentication session to persist.
     * @return [Result.success] if the operation succeeds, [Result.failure] otherwise.
     */
    suspend fun issue(authentication: Authentication): Result<Unit>

    /**
     * Terminates an existing authentication session identified by [refreshToken].
     *
     * @param refreshToken The refresh token of the session to revoke.
     * @return [Result.success] with `true` if a session was found and terminated,
     * `false` if no session was associated with the given token, or [Result.failure] if
     * an unexpected error occurred.
     */
    suspend fun terminate(refreshToken: RefreshToken): Result<Boolean>
}

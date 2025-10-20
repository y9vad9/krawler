package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedPlayerTag
import krawler.server.auth.domain.OwnershipChallenge
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Repository interface for managing authentication sessions.
 *
 * Provides methods to issue sessions, track attempts, mark success, and query session counts.
 */
interface AuthenticationSessionsRepository {

    /**
     * Issues a new authentication session for the given [tag], [token], and [challenge].
     *
     * @param tag Player tag associated with the session.
     * @param token Token identifying the session.
     * @param challenge Ownership challenge associated with this session.
     * @param currentTime Start time of the session.
     * @param duration Duration for which the session is valid.
     * @return [Result] wrapping [Unit] on success or failure.
     */
    suspend fun issueSession(
        tag: ChallengedPlayerTag,
        token: AuthenticationSessionToken,
        challenge: OwnershipChallenge,
        currentTime: Instant,
        duration: Duration,
    ): Result<Unit>

    /**
     * Returns the number of sessions within a given [timeline].
     *
     * @param timeline Time range to count sessions.
     * @param includeSuccessful Whether to include sessions marked as successful.
     * @param includeFailed Whether to include sessions marked as failed.
     * @return [Result] wrapping the count of sessions.
     */
    suspend fun getSessionsCount(
        timeline: ClosedRange<Instant>,
        includeSuccessful: Boolean,
        includeFailed: Boolean,
    ): Result<Int>

    /**
     * Returns the ownership challenge associated with given [token].
     */
    suspend fun getChallenge(token: AuthenticationSessionToken): Result<OwnershipChallenge?>

    /**
     * Records an authentication attempt for the session identified by [token].
     *
     * @param token Token of the session.
     * @return [Result] wrapping `true` if the session exists and the attempt was recorded,
     * `false` if no session with the given token was found.
     */
    suspend fun addAttempt(token: AuthenticationSessionToken): Result<Unit>

    /**
     * Marks the session identified by [token] as successful.
     *
     * @param token Token of the session.
     * @return [Result] wrapping `true` if the session exists and was successfully marked,
     * `false` if no session with the given token was found.
     */
    suspend fun markSuccess(token: AuthenticationSessionToken): Result<Boolean>
}

package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedBrawlStarsPlayerTag
import kotlin.time.Instant

/**
 * Generates authentication tokens for user sessions.
 *
 * Implementations typically handle creation of access and refresh tokens,
 * e.g., JWT for access tokens, securely randomized strings for refresh tokens.
 */
interface AuthenticationTokenGenerator {

    /**
     * Generates a new access token for the given [tag] with a specified [duration].
     *
     * @param tag The player tag associated with the access token.
     * @param expiresAt The lifetime of the access token.
     * @return A new [AccessToken] valid for the given duration.
     */
    fun generateAccessToken(tag: ChallengedBrawlStarsPlayerTag, expiresAt: Instant): AccessToken

    /**
     * Generates a new refresh token.
     *
     * @return A securely randomized [RefreshToken] that can be used to obtain new access tokens.
     */
    fun generateRefreshToken(): RefreshToken
}

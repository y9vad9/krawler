package krawler.server.auth.application

import krawler.server.auth.domain.ChallengedPlayerTag
import kotlin.time.Instant

/**
 * Represents an authenticated session for a player.
 *
 * @property accessToken Token used to access protected resources.
 * @property refreshToken Token used to refresh the access token.
 * @property issueTime Time when the authentication was issued.
 * @property expiresAt Time when the authentication expires.
 * @property playerTag Player tag associated with this authentication.
 */
class Authentication(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val issueTime: Instant,
    val expiresAt: Instant,
    val playerTag: ChallengedPlayerTag,
)

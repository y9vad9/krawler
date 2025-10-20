package krawler.server.auth.application

import kotlin.time.Instant

/**
 * Represents a bearer access token used to authenticate or authorize a user or session.
 *
 * The token is opaque and should be treated as sensitive information. It is typically
 * passed in HTTP headers to authenticate API requests.
 *
 * @property string The token value. Should not be logged or exposed in plain text.
 * @property expiresAt The exact instant when this access token becomes invalid.
 */
data class AccessToken(
    val string: String,
    val expiresAt: Instant
) {
    override fun toString(): String = "AccessToken(*****, $expiresAt)"
}

package krawler.server.auth.application

/**
 * Represents a refresh token used to obtain a new [AccessToken] without re-authenticating.
 *
 * Refresh tokens are long-lived and must be stored securely. They are typically exchanged
 * for a new access token when the current access token expires.
 *
 * @property string The token value.
 */
@JvmInline
value class RefreshToken(val string: String) {
    override fun toString(): String = "RefreshToken(*****)"
}

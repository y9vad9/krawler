package krawler.server.auth.application

/**
 * Wrapper for a session token.
 */
@JvmInline
value class AuthenticationSessionToken(val string: String) {
    override fun toString(): String = "AuthenticationSessionToken(*****)"
}

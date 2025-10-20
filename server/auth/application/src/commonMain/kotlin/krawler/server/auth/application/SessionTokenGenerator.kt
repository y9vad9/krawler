package krawler.server.auth.application

/**
 * Generates unique authentication session tokens.
 *
 * Implementations may use random or cryptographic generation
 * to ensure token uniqueness and unpredictability.
 */
interface SessionTokenGenerator {
    fun generate(): AuthenticationSessionToken
}

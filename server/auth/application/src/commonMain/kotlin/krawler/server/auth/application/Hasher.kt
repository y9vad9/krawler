package krawler.server.auth.application

/**
 * Provides cryptographic hash functions.
 */
interface Hasher {
    /**
     * Computes the SHA-512 hash of the given [string] and returns it as a hexadecimal string.
     *
     * @param string Input string to hash.
     * @return SHA-512 hash of the input.
     */
    fun sha512(string: String): String
}

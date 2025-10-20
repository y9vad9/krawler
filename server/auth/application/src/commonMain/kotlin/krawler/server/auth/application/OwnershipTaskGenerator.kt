package krawler.server.auth.application

import krawler.server.auth.domain.OwnershipTask

/**
 * Generates new ownership verification tasks used during authentication.
 *
 * Implementations should handle any randomness or variability required
 * to produce a valid [OwnershipTask].
 */
interface OwnershipTaskGenerator {
    fun generate(): OwnershipTask
}

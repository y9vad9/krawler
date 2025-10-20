package krawler.server.auth.application

import kotlin.uuid.Uuid

/**
 * Provides new universally unique identifiers (UUIDs).
 *
 * Used to abstract UUID generation for testing or deterministic scenarios.
 */
interface UuidProvider {
    fun provide(): Uuid
}

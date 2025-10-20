package krawler.server.auth.application

import kotlin.time.Instant

/**
 * Provides the current point in time.
 *
 * Used to abstract time retrieval for easier testing and determinism.
 */
interface TimeProvider {
    fun provide(): Instant
}

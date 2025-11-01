package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.MIN_VALUE

/**
 * Represents a unique identifier for a Star Power in Brawl Stars.
 *
 * This value class wraps an integer [rawInt] that corresponds to a specific Star Power
 * and provides type safety across the domain model. Valid Star Power IDs fall within
 * the range defined by [MIN_VALUE] to [MAX_VALUE].
 *
 * @property rawInt The underlying integer representation of the Star Power ID.
 */
@JvmInline
value class BrawlerStarPowerId(
    val rawInt: Int
) : Comparable<BrawlerStarPowerId> {

    init {
        require(rawInt in MIN_VALUE..MAX_VALUE) {
            "Invalid Star Power ID: $rawInt. Must be between $MIN_VALUE and $MAX_VALUE."
        }
    }

    override fun compareTo(other: BrawlerStarPowerId): Int = rawInt.compareTo(other.rawInt)

    override fun toString(): String = rawInt.toString()

    companion object {
        /** Minimum valid Star Power ID value. */
        const val MIN_VALUE: Int = 23_000_000

        /** Maximum valid Star Power ID value. */
        const val MAX_VALUE: Int = 23_001_000
    }
}

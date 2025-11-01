package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerGearId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerGearId.Companion.MIN_VALUE

/**
 * Represents a unique identifier for a Gear in Brawl Stars.
 *
 * This value class wraps an integer ID corresponding to a specific Gear.
 * The ID must fall within a valid range ([MIN_VALUE]..[MAX_VALUE]).
 *
 * @property value The raw integer identifier of the gear.
 */
@JvmInline
value class BrawlerGearId(val value: Int) : Comparable<BrawlerGearId> {

    init {
        require(value in VALUE_RANGE) {
            "Gear ID must be between $MIN_VALUE and $MAX_VALUE: $value"
        }
    }

    /** Minimum valid gear ID value. */
    companion object {
        const val MIN_VALUE: Int = 62_000_000

        /** Maximum valid gear ID value. */
        const val MAX_VALUE: Int = 62_001_000

        /** Inclusive range of valid gear IDs. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE
    }

    override fun compareTo(other: BrawlerGearId): Int = value.compareTo(other.value)
}

package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.MIN_VALUE

/**
 * Represents a unique identifier for a Gadget in Brawl Stars.
 *
 * This value class wraps an integer ID corresponding to a specific Gadget.
 * The ID must fall within a valid range ([MIN_VALUE]..[MAX_VALUE]).
 *
 * @property rawInt The raw integer identifier of the gadget.
 */
@JvmInline
value class BrawlerGadgetId(val rawInt: Int) : Comparable<BrawlerGadgetId> {

    init {
        require(rawInt in VALUE_RANGE) {
            "Gadget ID must be between $MIN_VALUE and $MAX_VALUE: $rawInt"
        }
    }

    /** Minimum valid gadget ID value. */
    companion object {
        const val MIN_VALUE: Int = 23_000_000

        /** Maximum valid gadget ID value. */
        const val MAX_VALUE: Int = 23_010_000

        /** Inclusive range of valid gadget IDs. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE
    }

    override fun compareTo(other: BrawlerGadgetId): Int = rawInt.compareTo(other.rawInt)

    override fun toString(): String = rawInt.toString()
}

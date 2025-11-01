package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerPowerLevel.Companion.MAX
import krawler.server.player.application.brawler.BrawlerPowerLevel.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerPowerLevel.Companion.MIN
import krawler.server.player.application.brawler.BrawlerPowerLevel.Companion.MIN_VALUE

/**
 * Represents a power level for a Brawler in Brawl Stars.
 *
 * This class wraps an integer in the range [MIN_VALUE] to [MAX_VALUE] and provides
 * type safety when working with brawler power levels. Use [MIN] and [MAX]
 * constants for convenient bounds.
 */
@JvmInline
value class BrawlerPowerLevel(
    /**
     * The underlying integer representation of the brawler's power level.
     *
     * Valid values range from [MIN_VALUE] (level 1) to [MAX_VALUE] (level 11).
     */
    val int: Int,
) : Comparable<BrawlerPowerLevel> {

    init {
        require(int in MIN_VALUE..MAX_VALUE) {
            "BrawlerPowerLevel must be between $MIN_VALUE and $MAX_VALUE, but was $int"
        }
    }

    override fun compareTo(other: BrawlerPowerLevel): Int = int.compareTo(other.int)

    override fun toString(): String = int.toString()

    companion object {
        /** Minimum allowed power level (level 1). */
        const val MIN_VALUE: Int = 1

        /** Maximum allowed power level (level 11). */
        const val MAX_VALUE: Int = 11

        /** Constant representing the minimum [BrawlerPowerLevel] (level 1). */
        val MIN: BrawlerPowerLevel = BrawlerPowerLevel(MIN_VALUE)

        /** Constant representing the maximum [BrawlerPowerLevel] (level 11). */
        val MAX: BrawlerPowerLevel = BrawlerPowerLevel(MAX_VALUE)
    }
}

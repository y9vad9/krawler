package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerRank.Companion.MAX
import krawler.server.player.application.brawler.BrawlerRank.Companion.MIN

/**
 * Represents a Brawler's rank in Brawl Stars.
 *
 * Ranks indicate a brawler's competitive progress and range from [MIN] to [MAX].
 *
 * @property raw The underlying integer rank value.
 */
@JvmInline
value class BrawlerRank(
    val raw: Int
) : Comparable<BrawlerRank> {

    init {
        require(raw in MIN_VALUE..MAX_VALUE) {
            "Invalid brawler rank: $raw. Must be between ${MIN.raw} and ${MAX.raw}."
        }
    }

    override fun compareTo(other: BrawlerRank): Int = raw.compareTo(other.raw)

    override fun toString(): String = raw.toString()

    companion object {
        private const val MIN_VALUE: Int = 1
        private const val MAX_VALUE: Int = 51

        /** Minimum valid Brawler rank (inclusive). */
        val MIN: BrawlerRank = BrawlerRank(MIN_VALUE)

        /** Maximum valid Brawler rank (inclusive). */
        val MAX: BrawlerRank = BrawlerRank(MAX_VALUE)
    }
}

package krawler.server.player.application.battle

/**
 * Represents a 1-based position in a Brawl Stars ranking.
 *
 * The [rawInt] must always be greater than or equal to 1.
 * Attempts to create a [RankingPosition] with an invalid value
 * will throw an [IllegalArgumentException].
 *
 * This class is type-safe and comparable by [rawInt].
 *
 * @property rawInt The underlying ranking position as a positive integer (≥ 1).
 * @throws IllegalArgumentException if [rawInt] is less than 1.
 */
@JvmInline
value class RankingPosition(val rawInt: Int) : Comparable<RankingPosition> {
    init {
        require(rawInt >= 1) { "Ranking position must be >= 1, got $rawInt" }
    }

    companion object {
        val FIRST: RankingPosition = RankingPosition(1)
        val SECOND: RankingPosition = RankingPosition(2)
        val THIRD: RankingPosition = RankingPosition(3)
        val FOURTH: RankingPosition = RankingPosition(4)
        val FIFTH: RankingPosition = RankingPosition(5)
    }

    override fun compareTo(other: RankingPosition): Int = rawInt.compareTo(other.rawInt)

    override fun toString(): String = rawInt.toString()
}

/**
 * Checks whether the ranking position is first.
 */
fun RankingPosition.isFirst(): Boolean = rawInt == 1

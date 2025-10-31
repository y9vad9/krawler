package krawler.server.player.application.battle

/**
 * Represents a 1-based position in a Brawl Stars ranking.
 *
 * This value class ensures that the internal integer is strictly positive (starting from 1).
 *
 * @property rawInt The underlying ranking position as a positive integer.
 */
@JvmInline
value class RankingPosition private constructor(val rawInt: Int) : Comparable<RankingPosition> {
    override fun compareTo(other: RankingPosition): Int = rawInt.compareTo(other.rawInt)

    companion object {
        /** The minimal valid position in rankings. */
        val FIRST: RankingPosition = RankingPosition(1)

        /** Second position in rankings. */
        val SECOND: RankingPosition = RankingPosition(2)

        /** Third position in rankings. */
        val THIRD: RankingPosition = RankingPosition(3)

        /** Fourth position in rankings. */
        val FOURTH: RankingPosition = RankingPosition(4)

        /** Fifth position in rankings. */
        val FIFTH: RankingPosition = RankingPosition(5)

        /**
         * Checks whether the given [value] is a valid [RankingPosition] (≥ 1).
         */
        fun isValid(value: Int): Boolean = value >= FIRST.rawInt

        /**
         * Creates a [RankingPosition] if the input [value] is valid.
         *
         * @return A [Result] containing a [RankingPosition] or [IllegalArgumentException] on failure.
         */
        fun create(value: Int): Result<RankingPosition> =
            if (isValid(value)) Result.success(RankingPosition(value))
            else Result.failure(IllegalArgumentException("Ranking position must be ≥ $FIRST."))

        /**
         * Creates a [RankingPosition] or throws [IllegalArgumentException] if invalid.
         */
        fun createOrThrow(value: Int): RankingPosition = create(value).getOrThrow()

        /**
         * Creates a [RankingPosition] or returns `null` if the [value] is invalid.
         */
        fun createOrNull(value: Int): RankingPosition? = create(value).getOrNull()
    }
}

/**
 * Checks whether the ranking position is first.
 */
fun RankingPosition.isFirst(): Boolean = rawInt == 1

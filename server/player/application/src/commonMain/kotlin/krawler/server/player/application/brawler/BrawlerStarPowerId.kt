package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.MIN_VALUE
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.VALUE_RANGE
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.create
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.createOrNull
import krawler.server.player.application.brawler.BrawlerStarPowerId.Companion.createOrThrow

/**
 * Represents a unique identifier for a Star Power in Brawl Stars.
 *
 * This value class wraps an integer [rawInt] that corresponds to a specific Star Power
 * and provides type safety across the domain model. Valid Star Power IDs fall within
 * the range defined by [MIN_VALUE] to [MAX_VALUE].
 *
 * Use [create], [createOrThrow], or [createOrNull] to safely construct an instance.
 *
 * @property rawInt The underlying integer representation of the Star Power ID.
 */
@JvmInline
value class BrawlerStarPowerId private constructor(
    val rawInt: Int,
) : Comparable<BrawlerStarPowerId> {

    /**
     * Provides constants and factory methods for [BrawlerStarPowerId].
     */
    companion object {
        /** The minimum valid Star Power ID value. */
        const val MIN_VALUE: Int = 23_000_000

        /** The maximum valid Star Power ID value. */
        const val MAX_VALUE: Int = 23_001_000

        /** The inclusive range of valid Star Power ID values. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Checks whether the given [input] integer is a valid Star Power ID.
         *
         * @param input The integer to validate.
         * @return `true` if [input] falls within [VALUE_RANGE], `false` otherwise.
         */
        fun isValid(input: Int): Boolean =
            input in VALUE_RANGE

        /**
         * Attempts to create a [BrawlerStarPowerId] from the given [input].
         *
         * @param input The integer ID to convert.
         * @return A [Result] containing a [BrawlerStarPowerId] on success,
         * or a failure with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<BrawlerStarPowerId> =
            if (isValid(input)) Result.success(BrawlerStarPowerId(input))
            else Result.failure(IllegalArgumentException("Invalid Star Power ID: $input"))

        /**
         * Creates a [BrawlerStarPowerId] from [input], or throws [IllegalArgumentException] if invalid.
         *
         * @param input The integer ID to convert.
         * @return A valid [BrawlerStarPowerId].
         * @throws IllegalArgumentException If [input] is outside [VALUE_RANGE].
         */
        fun createOrThrow(input: Int): BrawlerStarPowerId =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlerStarPowerId] from [input], or returns `null` if [input] is invalid.
         *
         * @param input The integer ID to convert.
         * @return A valid [BrawlerStarPowerId], or `null` if invalid.
         */
        fun createOrNull(input: Int): BrawlerStarPowerId? =
            create(input).getOrNull()
    }

    /**
     * Compares this ID with another [BrawlerStarPowerId] by their numeric [rawInt].
     *
     * @param other The ID to compare against.
     * @return Result of comparing the two integer values.
     */
    override fun compareTo(other: BrawlerStarPowerId): Int = rawInt.compareTo(other.rawInt)
}

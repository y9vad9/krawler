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
value class BrawlerPowerLevel private constructor(
    /**
     * The underlying integer representation of the brawler's power level.
     *
     * Valid values range from [MIN_VALUE] (level 0) to [MAX_VALUE] (level 11).
     */
    val int: Int,
) : Comparable<BrawlerPowerLevel> {

    /**
     * Compares this [BrawlerPowerLevel] with another based on their integer values.
     *
     * @param other The other [BrawlerPowerLevel] to compare against.
     * @return A negative integer, zero, or a positive integer as this level
     * is less than, equal to, or greater than the other level.
     */
    override fun compareTo(other: BrawlerPowerLevel): Int = int.compareTo(other.int)

    /**
     * Returns underlying [int] string representation.
     */
    override fun toString(): String = int.toString()

    /** Constants with constraints and validation */
    companion object {
        /**
         * Minimum allowed power level.
         *
         * This corresponds to level 0.
         */
        const val MIN_VALUE: Int = 1

        /**
         * Maximum allowed power level.
         *
         * This corresponds to level 11, which is the current upgrade cap in the game.
         */
        const val MAX_VALUE: Int = 11

        /**
         * Value range between [MIN] and [MAX].
         */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Constant representing the minimum [BrawlerPowerLevel] (level 0).
         */
        val MIN: BrawlerPowerLevel = BrawlerPowerLevel(MIN_VALUE)

        /**
         * Constant representing the maximum [BrawlerPowerLevel] (level 11).
         */
        val MAX: BrawlerPowerLevel = BrawlerPowerLevel(MAX_VALUE)

        /**
         * Checks if the given [input] is within the valid power level range.
         *
         * @param input The integer value to validate.
         * @return `true` if [input] is between [MIN_VALUE] and [MAX_VALUE], inclusive.
         */
        fun isValid(input: Int): Boolean =
            input in VALUE_RANGE

        /**
         * Attempts to create a [BrawlerPowerLevel] from the given [input].
         *
         * Returns a [Result.success] if [input] is valid, or a [Result.failure] containing
         * an [IllegalArgumentException] if invalid.
         *
         * @param input The integer value to create the power level from.
         */
        fun create(input: Int): Result<BrawlerPowerLevel> =
            if (isValid(input)) Result.success(BrawlerPowerLevel(input))
            else Result.failure(IllegalArgumentException("Invalid BrawlerPowerLevel: $input"))

        /**
         * Creates a [BrawlerPowerLevel] from [input] or throws [IllegalArgumentException] if invalid.
         *
         * @param input The integer value to create the power level from.
         * @throws IllegalArgumentException if [input] is out of range.
         */
        fun createOrThrow(input: Int): BrawlerPowerLevel =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlerPowerLevel] from [input], or returns `null` if invalid.
         *
         * @param input The integer value to create the power level from.
         */
        fun createOrNull(input: Int): BrawlerPowerLevel? =
            create(input).getOrNull()
    }
}

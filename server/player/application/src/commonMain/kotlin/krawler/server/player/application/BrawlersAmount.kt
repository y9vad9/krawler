package krawler.server.player.application

import krawler.server.player.application.BrawlersAmount.Companion.create
import krawler.server.player.application.BrawlersAmount.Companion.createOrNull
import krawler.server.player.application.BrawlersAmount.Companion.createOrThrow

/**
 * Represents the number of brawlers a Brawl Stars player has.
 *
 * Use the [create], [createOrThrow], or [createOrNull] methods to construct valid instances.
 *
 * @property int The raw integer value of victories (guaranteed to be ≥ 0).
 */
@JvmInline
value class BrawlersAmount private constructor(val int: Int) : Comparable<BrawlersAmount> {

    companion object {
        private const val ERROR = "Victory count must be zero or greater."

        /**
         * Checks whether the given victory count is valid.
         *
         * A valid victory count is a non-negative integer.
         *
         * @param input The number of victories to validate.
         * @return `true` if the input is valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean = input >= 0

        /**
         * Creates a [BrawlersAmount] if the [input] is valid (non-negative).
         *
         * @param input The number of victories to wrap.
         * @return [Result.success] with [BrawlersAmount], or [Result.failure]
         * with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<BrawlersAmount> =
            if (isValid(input)) Result.success(BrawlersAmount(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [BrawlersAmount] or throws [IllegalArgumentException] if the input is invalid.
         *
         * @param input The number of victories to wrap.
         * @return A valid [BrawlersAmount] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): BrawlersAmount =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlersAmount], or returns `null` if the input is invalid.
         *
         * @param input The number of victories to wrap.
         * @return A valid [BrawlersAmount], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): BrawlersAmount? =
            create(input).getOrNull()
    }

    /**
     * Compares this [BrawlersAmount] with another by numeric value.
     */
    override fun compareTo(other: BrawlersAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [BrawlersAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [BrawlersAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: BrawlersAmount): BrawlersAmount =
        createOrThrow(this.int + other.int)

    /**
     * Subtracts another [BrawlersAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [BrawlersAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: BrawlersAmount): BrawlersAmount =
        createOrThrow(this.int - other.int)

    /**
     * Returns the string representation of the victory count.
     */
    override fun toString(): String = int.toString()
}

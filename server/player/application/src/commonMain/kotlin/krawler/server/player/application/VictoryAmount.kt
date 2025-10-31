package krawler.server.player.application

import krawler.server.player.application.VictoryAmount.Companion.create
import krawler.server.player.application.VictoryAmount.Companion.createOrNull
import krawler.server.player.application.VictoryAmount.Companion.createOrThrow

/**
 * Represents the number of victories a Brawl Stars player has achieved.
 *
 * Victories are accumulated through successful battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * Use the [create], [createOrThrow], or [createOrNull] methods to construct valid instances.
 *
 * @property int The raw integer value of victories (guaranteed to be ≥ 0).
 */
@JvmInline
value class VictoryAmount private constructor(val int: Int) : Comparable<VictoryAmount> {

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
         * Creates a [VictoryAmount] if the [input] is valid (non-negative).
         *
         * @param input The number of victories to wrap.
         * @return [Result.success] with [VictoryAmount], or [Result.failure]
         * with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<VictoryAmount> =
            if (isValid(input)) Result.success(VictoryAmount(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [VictoryAmount] or throws [IllegalArgumentException] if the input is invalid.
         *
         * @param input The number of victories to wrap.
         * @return A valid [VictoryAmount] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): VictoryAmount =
            create(input).getOrThrow()

        /**
         * Creates a [VictoryAmount], or returns `null` if the input is invalid.
         *
         * @param input The number of victories to wrap.
         * @return A valid [VictoryAmount], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): VictoryAmount? =
            create(input).getOrNull()
    }

    /**
     * Compares this [VictoryAmount] with another by numeric value.
     */
    override fun compareTo(other: VictoryAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [VictoryAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [VictoryAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: VictoryAmount): VictoryAmount =
        createOrThrow(this.int + other.int)

    /**
     * Subtracts another [VictoryAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [VictoryAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: VictoryAmount): VictoryAmount =
        createOrThrow(this.int - other.int)

    /**
     * Returns the string representation of the victory count.
     */
    override fun toString(): String = int.toString()
}

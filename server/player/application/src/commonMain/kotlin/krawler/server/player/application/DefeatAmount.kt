package krawler.server.player.application

import krawler.server.player.application.DefeatAmount.Companion.create
import krawler.server.player.application.DefeatAmount.Companion.createOrNull
import krawler.server.player.application.DefeatAmount.Companion.createOrThrow

/**
 * Represents the number of defeats a Brawl Stars player has achieved.
 *
 * Defeats are accumulated through lost battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * Use the [create], [createOrThrow], or [createOrNull] methods to construct valid instances.
 *
 * @property int The raw integer value of defeats (guaranteed to be ≥ 0).
 */
@JvmInline
value class DefeatAmount private constructor(val int: Int) : Comparable<DefeatAmount> {

    companion object {
        private const val ERROR = "Defeat count must be zero or greater."

        /**
         * Checks whether the given defeat count is valid.
         *
         * A valid Defeat count is a non-negative integer.
         *
         * @param input The number of defeats to validate.
         * @return `true` if the input is valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean = input >= 0

        /**
         * Creates a [DefeatAmount] if the [input] is valid (non-negative).
         *
         * @param input The number of defeats to wrap.
         * @return [Result.success] with [DefeatAmount], or [Result.failure]
         * with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<DefeatAmount> =
            if (isValid(input)) Result.success(DefeatAmount(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [DefeatAmount] or throws [IllegalArgumentException] if the input is invalid.
         *
         * @param input The number of defeats to wrap.
         * @return A valid [DefeatAmount] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): DefeatAmount =
            create(input).getOrThrow()

        /**
         * Creates a [DefeatAmount], or returns `null` if the input is invalid.
         *
         * @param input The number of defeats to wrap.
         * @return A valid [DefeatAmount], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): DefeatAmount? =
            create(input).getOrNull()
    }

    /**
     * Compares this [DefeatAmount] with another by numeric value.
     */
    override fun compareTo(other: DefeatAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [DefeatAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [DefeatAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: DefeatAmount): DefeatAmount =
        createOrThrow(this.int + other.int)

    /**
     * Subtracts another [DefeatAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [DefeatAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: DefeatAmount): DefeatAmount =
        createOrThrow(this.int - other.int)

    /**
     * Returns the string representation of the Defeat count.
     */
    override fun toString(): String = int.toString()
}

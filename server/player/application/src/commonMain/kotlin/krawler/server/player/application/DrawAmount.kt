package krawler.server.player.application

/**
 * Represents the number of draws a Brawl Stars player has achieved.
 *
 * Draws are accumulated through successful battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * Use the [create], [createOrThrow], or [createOrNull] methods to construct valid instances.
 *
 * @property int The raw integer value of draws (guaranteed to be ≥ 0).
 */
@JvmInline
value class DrawAmount private constructor(val int: Int) : Comparable<DrawAmount> {

    companion object {
        private const val ERROR = "Draw count must be zero or greater."

        /**
         * Checks whether the given defeat count is valid.
         *
         * A valid Draw count is a non-negative integer.
         *
         * @param input The number of draws to validate.
         * @return `true` if the input is valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean = input >= 0

        /**
         * Creates a [DrawAmount] if the [input] is valid (non-negative).
         *
         * @param input The number of draws to wrap.
         * @return [Result.success] with [DrawAmount], or [Result.failure]
         * with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<DrawAmount> =
            if (isValid(input)) Result.success(DrawAmount(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [DrawAmount] or throws [IllegalArgumentException] if the input is invalid.
         *
         * @param input The number of draws to wrap.
         * @return A valid [DrawAmount] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): DrawAmount =
            create(input).getOrThrow()

        /**
         * Creates a [DrawAmount], or returns `null` if the input is invalid.
         *
         * @param input The number of draws to wrap.
         * @return A valid [DrawAmount], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): DrawAmount? =
            create(input).getOrNull()
    }

    /**
     * Compares this [DrawAmount] with another by numeric value.
     */
    override fun compareTo(other: DrawAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [DrawAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [DrawAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: DrawAmount): DrawAmount =
        createOrThrow(this.int + other.int)

    /**
     * Subtracts another [DrawAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [DrawAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: DrawAmount): DrawAmount =
        createOrThrow(this.int - other.int)

    /**
     * Returns the string representation of the Draw count.
     */
    override fun toString(): String = int.toString()
}

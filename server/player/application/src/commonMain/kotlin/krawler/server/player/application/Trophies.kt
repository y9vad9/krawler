package krawler.server.player.application

/**
 * Represents the number of trophies a Brawl Stars player has.
 *
 * Trophies are a core metric representing player progress and skill
 * in various game modes. This value class provides type safety and
 * comparability based on the integer trophy count.
 *
 * Note: This class does not enforce validation on the trophy count.
 * It assumes the value is meaningful within the game's context.
 *
 * @property rawInt The raw trophy count.
 */
@JvmInline
value class Trophies private constructor(val rawInt: Int) : Comparable<Trophies> {

    /**
     * Compares this trophy count with another.
     *
     * @param other The other [Trophies] instance to compare to.
     * @return A negative integer, zero, or positive integer depending on comparison result.
     */
    override fun compareTo(other: Trophies): Int = rawInt.compareTo(other.rawInt)

    /**
     * Adds another trophy count to this one.
     *
     * @param other The other [Trophies] to add.
     * @return A new [Trophies] instance representing the total.
     */
    operator fun plus(other: Trophies): Trophies = createOrThrow(this.rawInt + other.rawInt)

    /**
     * Returns the trophy count as a string.
     */
    override fun toString(): String = rawInt.toString()

    companion object {
        /** Minimum valid trophy count. */
        const val MIN_VALUE: Int = 0

        /**
         * Returns `true` if the given [value] is valid for [Trophies].
         */
        fun isValid(value: Int): Boolean = value >= MIN_VALUE

        /**
         * Returns a [Trophies] instance if the [value] is valid; otherwise `null`.
         */
        fun createOrNull(value: Int): Trophies? =
            if (isValid(value)) Trophies(value) else null

        /**
         * Returns a [Result] containing a [Trophies] instance or [IllegalArgumentException].
         */
        fun create(value: Int): Result<Trophies> =
            if (isValid(value)) Result.success(Trophies(value))
            else Result.failure(IllegalArgumentException("Invalid trophy count: $value"))

        /**
         * Returns a [Trophies] instance or throws [IllegalArgumentException] if invalid.
         */
        fun createOrThrow(value: Int): Trophies =
            create(value).getOrThrow()
    }
}

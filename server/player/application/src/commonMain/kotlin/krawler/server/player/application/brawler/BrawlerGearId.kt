package krawler.server.player.application.brawler

/**
 * Represents a unique identifier for a Gear in Brawl Stars.
 *
 * Gears are passive upgrades that enhance a brawler's performance in various ways
 * (e.g., faster reload speed, healing boosts, etc.). Each gear has an associated
 * ID within a known numeric range, defined by the game.
 *
 * This value class enforces a valid ID range and provides utility methods to validate
 * or construct safe instances of [BrawlerGearId].
 *
 * @property value The raw integer identifier of the gear.
 */
@JvmInline
value class BrawlerGearId private constructor(
    /**
     * The raw integer identifier of the gear.
     */
    val value: Int,
) : Comparable<BrawlerGearId> {
    /** Constants with constraints and validation */
    companion object {
        /**
         * The minimum valid gear ID value.
         */
        const val MIN_VALUE: Int = 23_000_000

        /**
         * The maximum valid gear ID value.
         */
        const val MAX_VALUE: Int = 23_001_000

        /**
         * The inclusive range of valid gear ID values.
         */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Returns `true` if the [input] is within the valid gear ID range.
         *
         * @param input The raw ID to check.
         * @return `true` if valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean =
            input in VALUE_RANGE

        /**
         * Attempts to create a [BrawlerGearId] from the provided [input].
         *
         * @param input The raw integer gear ID.
         * @return [Result.success] if valid, [Result.failure] with [IllegalArgumentException] otherwise.
         */
        fun create(input: Int): Result<BrawlerGearId> =
            if (isValid(input)) Result.success(BrawlerGearId(input))
            else Result.failure(IllegalArgumentException("Invalid gear ID: $input"))

        /**
         * Creates a [BrawlerGearId] from the given [input], or throws an [IllegalArgumentException] if invalid.
         *
         * @param input The raw gear ID.
         * @return A valid [BrawlerGearId] instance.
         * @throws IllegalArgumentException If the input is invalid.
         */
        fun createOrThrow(input: Int): BrawlerGearId =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlerGearId] from the given [input], or returns `null` if invalid.
         *
         * @param input The raw gear ID.
         * @return A valid [BrawlerGearId] or `null`.
         */
        fun createOrNull(input: Int): BrawlerGearId? =
            create(input).getOrNull()
    }

    /**
     * Compares this [BrawlerGearId] with another [BrawlerGearId] by their raw integer values.
     *
     * @param other The other gear ID to compare against.
     * @return A negative value if this is less, zero if equal, or positive if greater.
     */
    override fun compareTo(other: BrawlerGearId): Int = value.compareTo(other.value)
}

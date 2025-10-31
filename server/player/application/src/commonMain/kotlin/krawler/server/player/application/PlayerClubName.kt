package krawler.server.player.application

import krawler.server.player.application.PlayerClubName.Companion.LENGTH_RANGE
import krawler.server.player.application.PlayerClubName.Companion.create

/**
 * Represents a Brawl Stars club name, as displayed in-game.
 *
 * A valid club name:
 * - Must be at least [Companion.MIN_LENGTH] character long.
 * - Must not exceed [Companion.MAX_LENGTH] characters.
 * - Can contain any characters, including emojis and symbols, as long as the length is within the valid range.
 *
 * @property rawString The raw representation of the club name.
 *
 * Use [Companion.create], [Companion.createOrNull], or [Companion.createOrThrow]
 * to safely construct a [PlayerClubName] instance.
 */
@JvmInline
value class PlayerClubName private constructor(
    /** The validated club name string, as accepted by the Brawl Stars API. */
    val rawString: String,
) : Comparable<PlayerClubName> {
    /** Companion object containing validation logic and factory methods. */
    companion object {
        /** The minimum allowed length of a club name. */
        const val MIN_LENGTH: Int = 1

        /** The maximum allowed length of a club name. */
        const val MAX_LENGTH: Int = 15

        /** The valid range of length for a club name. */
        val LENGTH_RANGE: IntRange = MIN_LENGTH..MAX_LENGTH

        /**
         * Returns `true` if the given [input] string is a valid Brawl Stars club name,
         * according to the length constraints in [LENGTH_RANGE].
         *
         * @param input The club name string to validate.
         * @return `true` if valid, otherwise `false`.
         */
        fun isValid(input: String): Boolean =
            input.length in LENGTH_RANGE

        /**
         * Attempts to create a [PlayerClubName] from the given [input].
         *
         * @param input The club name string to validate and wrap.
         * @return [Result.success] containing a valid [PlayerClubName] if the input passes validation,
         * or [Result.failure] with an [IllegalArgumentException] if invalid.
         */
        fun create(input: String): Result<PlayerClubName> =
            if (isValid(input)) Result.success(PlayerClubName(input))
            else Result.failure(IllegalArgumentException("Invalid club name length: $input"))

        /**
         * Creates a [PlayerClubName] from [input] or throws [IllegalArgumentException] if invalid.
         *
         * This is a convenience wrapper around [create].
         *
         * @param input The club name string to validate and wrap.
         * @return A valid [PlayerClubName].
         * @throws IllegalArgumentException if [input] is invalid.
         */
        fun createOrThrow(input: String): PlayerClubName =
            create(input).getOrThrow()

        /**
         * Creates a [PlayerClubName] from [input] or returns `null` if invalid.
         *
         * This is a convenience wrapper around [create].
         *
         * @param input The club name string to validate and wrap.
         * @return A valid [PlayerClubName] or `null` if invalid.
         */
        fun createOrNull(input: String): PlayerClubName? =
            create(input).getOrNull()
    }

    /**
     * Compares this instance of [PlayerClubName] to the [other] of type [PlayerClubName].
     * Returns result of underlying [rawString] being compared.
     */
    override fun compareTo(other: PlayerClubName): Int = rawString.compareTo(other.rawString)

    /**
     * Returns the raw string representation of this club name.
     *
     * @return The validated club name string.
     */
    override fun toString(): String = rawString
}

package krawler.server.player.application

import krawler.server.player.application.PlayerExpLevel.Companion.create
import krawler.server.player.application.PlayerExpLevel.Companion.createOrNull
import krawler.server.player.application.PlayerExpLevel.Companion.createOrThrow

/**
 * Represents the account experience level of a Brawl Stars player.
 *
 * In Brawl Stars, experience levels (distinct from XP tokens) reflect account progression based
 * on the cumulative experience earned over time by playing matches, completing quests, earning
 * "Star Player" bonuses, and participating in events. Experience level milestones were historically
 * associated with rewards such as Tokens and Profile Icons.
 *
 * This value class wraps an integer representation of the experience level and allows comparison
 * between levels using natural order.
 *
 * Note: Experience levels do **not** affect matchmaking, brawler strength, or competitive ranking.
 *
 * [Learn more](https://brawlstars.fandom.com/wiki/Experience).
 *
 * @property int The numeric experience level.
 */
@JvmInline
value class PlayerExpLevel private constructor(
    val int: Int
) : Comparable<PlayerExpLevel> {

    /**
     * Compares this experience level to another.
     *
     * @param other The other [PlayerExpLevel] to compare against.
     * @return Negative if this < other, zero if equal, positive if this > other.
     */
    override fun compareTo(other: PlayerExpLevel): Int =
        int.compareTo(other.int)

    /**
     * Provides factory methods and validation logic for constructing [PlayerExpLevel] instances.
     *
     * Use this companion to safely create instances of [PlayerExpLevel], ensuring the level is non-negative.
     * It offers multiple creation methods depending on the desired error-handling strategy:
     *
     * - [create]: Returns a [Result] that encapsulates success or failure.
     * - [createOrThrow]: Throws an [IllegalArgumentException] if the input is invalid.
     * - [createOrNull]: Returns `null` if the input is invalid.
     *
     * This design enforces domain invariants and prevents creation of invalid [PlayerExpLevel] values.
     */
    companion object {
        private const val ERROR = "Player experience level must be zero or greater."

        /**
         * Checks whether the given experience level is valid.
         *
         * A valid experience level is a non-negative integer.
         *
         * @param input The experience level to validate.
         * @return `true` if the level is valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean = input >= 0

        /**
         * Creates a [PlayerExpLevel] if the [input] is valid (non-negative).
         *
         * @param input The experience level to wrap.
         * @return [Result.success] with [PlayerExpLevel], or [Result.failure]
         * with [IllegalArgumentException] if invalid.
         */
        fun create(input: Int): Result<PlayerExpLevel> =
            if (isValid(input)) Result.success(PlayerExpLevel(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [PlayerExpLevel], or throws if the [input] is invalid.
         *
         * @param input The experience level to wrap.
         * @return A valid [PlayerExpLevel] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): PlayerExpLevel =
            create(input).getOrThrow()

        /**
         * Creates a [PlayerExpLevel], or returns `null` if the [input] is invalid.
         *
         * @param input The experience level to wrap.
         * @return A valid [PlayerExpLevel], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): PlayerExpLevel? =
            create(input).getOrNull()
    }
}

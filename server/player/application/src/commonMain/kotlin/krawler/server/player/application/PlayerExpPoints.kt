package krawler.server.player.application

import krawler.server.player.application.PlayerExpPoints.Companion.create
import krawler.server.player.application.PlayerExpPoints.Companion.createOrNull
import krawler.server.player.application.PlayerExpPoints.Companion.createOrThrow

/**
 * Represents the experience points (XP) earned by a Brawl Stars player.
 *
 * Each match completed—regardless of outcome—contributes XP to a player's account,
 * which is used to advance account level and unlock cosmetic and token rewards.
 * Experience Points are capped daily and do **not** impact matchmaking or gameplay performance.
 *
 * Experience was later replaced by Mastery Points in February 2023 for individual brawler progression,
 * but account-level XP remains visible and relevant in player profiles.
 *
 * @property int The raw integer amount of XP earned.
 * @see <a href="https://brawlstars.fandom.com/wiki/Experience">Brawl Stars Experience - Fandom Wiki</a>
 */
@JvmInline
value class PlayerExpPoints private constructor(
    val int: Int
) : Comparable<PlayerExpPoints> {

    /**
     * Compares this [PlayerExpPoints] with another based on their numeric value.
     *
     * @param other The other XP value to compare against.
     * @return Negative if this < other, zero if equal, positive if this > other.
     */
    override fun compareTo(other: PlayerExpPoints): Int = int.compareTo(other.int)

    /**
     * Provides factory methods and validation logic for constructing [PlayerExpPoints] instances.
     *
     * Use this companion to safely create instances of [PlayerExpPoints], ensuring the XP is non-negative.
     * It offers multiple creation methods depending on your error-handling strategy:
     *
     * - [create]: Returns a [Result] encapsulating success or failure.
     * - [createOrThrow]: Throws [IllegalArgumentException] if input is invalid.
     * - [createOrNull]: Returns `null` on invalid input.
     */
    companion object {
        private const val ERROR = "XP value must be zero or greater."

        /**
         * Checks whether the given value is a valid XP amount.
         *
         * XP must be zero or a positive integer.
         *
         * @param input The XP value to validate.
         * @return `true` if the value is valid, `false` otherwise.
         */
        fun isValid(input: Int): Boolean = input >= 0

        /**
         * Creates a [PlayerExpPoints] if the input is valid (non-negative).
         *
         * @param input The XP value to wrap.
         * @return [Result.success] with [PlayerExpPoints], or [Result.failure] if invalid.
         */
        fun create(input: Int): Result<PlayerExpPoints> =
            if (isValid(input)) Result.success(PlayerExpPoints(input))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [PlayerExpPoints] or throws if input is invalid.
         *
         * @param input The XP value to wrap.
         * @return A valid [PlayerExpPoints] instance.
         * @throws IllegalArgumentException if [input] is negative.
         */
        fun createOrThrow(input: Int): PlayerExpPoints =
            create(input).getOrThrow()

        /**
         * Creates a [PlayerExpPoints], or returns `null` if input is invalid.
         *
         * @param input The XP value to wrap.
         * @return A valid [PlayerExpPoints], or `null` if [input] is negative.
         */
        fun createOrNull(input: Int): PlayerExpPoints? =
            create(input).getOrNull()
    }
}

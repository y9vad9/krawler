package krawler.server.player.application

import krawler.server.player.application.PlayerClubTag.Companion.MAX_LENGTH
import krawler.server.player.application.PlayerClubTag.Companion.MIN_LENGTH
import krawler.server.player.application.PlayerClubTag.Companion.REGEX

/**
 * Represents a validated Brawl Stars **club tag**, used to uniquely identify clubs within the game.
 *
 * Club tags in Brawl Stars:
 * - Are typically **9 characters** long, but can range from [MIN_LENGTH] to [MAX_LENGTH] characters.
 * - May optionally start with `#`.
 * - Contain only uppercase letters (`A–Z`) and digits (`0–9`).
 *
 * This type ensures valid format using [REGEX] at construction time,
 * and provides safe factory methods to avoid invalid states.
 *
 * Internally, the tag is stored as-is (including `#` if present), and utilities
 * are available to access both the raw and normalized versions.
 */
@JvmInline
value class PlayerClubTag private constructor(
    private val rawString: String,
) {
    /**
     * Returns the tag string with a leading `#`, even if not originally present.
     *
     * Useful for display or when sending to the Brawl Stars API.
     *
     * @return The tag string prefixed with `#`.
     */
    val stringWithTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString else "#$rawString"

    /**
     * Returns the tag string without the leading `#`.
     *
     * Useful for comparisons, storage, or internal usage.
     *
     * @return The tag string without the `#` prefix.
     */
    val stringWithoutTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString.substring(1) else rawString

    /**
     * Returns the canonical display format of the tag, always prefixed with `#`.
     *
     * @return The tag string with leading `#`.
     * @see stringWithTagPrefix
     */
    override fun toString(): String = stringWithTagPrefix

    /** Constants and validation */
    companion object {
        /** Minimum allowed length for a tag, excluding `#`. */
        const val MIN_LENGTH: Int = 3

        /** Maximum allowed length for a tag, excluding `#`. */
        const val MAX_LENGTH: Int = 14

        /**
         * Regular expression to validate club tags.
         *
         * Accepts optional `#`, allows only uppercase A–Z and digits 0–9,
         * with length between [MIN_LENGTH] and [MAX_LENGTH].
         * Case-insensitive.
         */
        val REGEX: Regex = Regex(
            pattern = "^#?[A-Z0-9]{$MIN_LENGTH,$MAX_LENGTH}$",
            option = RegexOption.IGNORE_CASE,
        )

        /**
         * Validates whether the [input] string is a valid club tag.
         *
         * Case-insensitive.
         *
         * @param input The tag string to validate, with or without leading `#`.
         * @return `true` if [input] is a valid club tag, otherwise `false`.
         */
        fun isValid(input: String): Boolean =
            REGEX.matches(input)

        /**
         * Attempts to create a [PlayerClubTag] from the given [input].
         *
         * The input is uppercased and validated using [REGEX].
         *
         * @param input The tag string to create a [PlayerClubTag] from.
         * @return [Result.success] with a valid [PlayerClubTag] if input is valid,
         *         otherwise [Result.failure] with an [IllegalArgumentException].
         */
        fun create(input: String): Result<PlayerClubTag> {
            val normalized = input.uppercase()
            return if (isValid(normalized)) Result.success(PlayerClubTag(normalized))
            else Result.failure(IllegalArgumentException("Invalid club tag: $input"))
        }

        /**
         * Creates a [PlayerClubTag] from [input] or throws [IllegalArgumentException] if invalid.
         *
         * @param input The tag string to create a [PlayerClubTag] from.
         * @return A valid [PlayerClubTag] instance.
         * @throws IllegalArgumentException if [input] is invalid.
         */
        fun createOrThrow(input: String): PlayerClubTag =
            create(input).getOrThrow()

        /**
         * Creates a [PlayerClubTag] from [input] or returns `null` if invalid.
         *
         * @param input The tag string to create a [PlayerClubTag] from.
         * @return A valid [PlayerClubTag], or `null` if invalid.
         */
        fun createOrNull(input: String): PlayerClubTag? =
            create(input).getOrNull()
    }
}

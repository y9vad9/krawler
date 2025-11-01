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
     */
    val stringWithTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString else "#$rawString"

    /**
     * Returns the tag string without the leading `#`.
     *
     * Useful for comparisons, storage, or internal usage.
     */
    val stringWithoutTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString.substring(1) else rawString

    /**
     * Returns the canonical display format of the tag, always prefixed with `#`.
     */
    override fun toString(): String = stringWithTagPrefix

    /** Validation constants and factories */
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
         * Attempts to create a [PlayerClubTag] from the given [input].
         *
         * Returns a [FactoryResult] describing whether the tag is valid.
         * The input is normalized to uppercase before validation.
         *
         * @param input The tag string to validate.
         * @return A [FactoryResult] describing the outcome.
         */
        fun create(input: String): FactoryResult {
            if (input.isBlank()) return FactoryResult.EmptyInput

            val normalized = input.uppercase()
            val value = normalized.removePrefix("#")

            return when {
                value.length !in MIN_LENGTH..MAX_LENGTH ->
                    FactoryResult.InvalidLength

                !REGEX.matches(normalized) ->
                    FactoryResult.InvalidFormat

                else ->
                    FactoryResult.Success(PlayerClubTag(normalized))
            }
        }

        /**
         * Creates a [PlayerClubTag] or throws an [IllegalArgumentException]
         * with a descriptive message based on [FactoryResult].
         */
        fun createOrThrow(input: String): PlayerClubTag =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                FactoryResult.EmptyInput ->
                    throw IllegalArgumentException("Club tag cannot be empty or blank.")

                FactoryResult.InvalidLength ->
                    throw IllegalArgumentException(
                        "Club tag length must be between $MIN_LENGTH and" +
                                " $MAX_LENGTH (excluding '#')."
                    )

                FactoryResult.InvalidFormat ->
                    throw IllegalArgumentException(
                        "Club tag may only contain A–Z and 0–9 characters, optionally " +
                                "prefixed with '#'."
                    )
            }

        /**
         * Creates a [PlayerClubTag], or returns `null` if the [input] is invalid.
         */
        fun createOrNull(input: String): PlayerClubTag? =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                else -> null
            }
    }

    /**
     * Type-safe factory result for [PlayerClubTag] creation.
     */
    sealed interface FactoryResult {
        /** Successful creation with a valid tag. */
        data class Success(val value: PlayerClubTag) : FactoryResult

        /** Tag was empty or blank. */
        data object EmptyInput : FactoryResult

        /** Tag contained invalid characters or format. */
        data object InvalidFormat : FactoryResult

        /** Tag length outside allowed range. */
        data object InvalidLength : FactoryResult
    }
}

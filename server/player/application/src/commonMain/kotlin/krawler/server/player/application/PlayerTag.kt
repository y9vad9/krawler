package krawler.server.player.application

import krawler.server.player.application.PlayerTag.Companion.MAX_LENGTH
import krawler.server.player.application.PlayerTag.Companion.MIN_LENGTH
import krawler.server.player.application.PlayerTag.Companion.REGEX
import krawler.server.player.application.PlayerTag.Companion.create
import krawler.server.player.application.PlayerTag.Companion.createOrNull
import krawler.server.player.application.PlayerTag.Companion.createOrThrow

/**
 * Represents a Brawl Stars player tag, used to uniquely identify players across the API.
 *
 * A valid player tag:
 * - May start with an optional `#` symbol.
 * - Must contain only uppercase letters A–Z and digits 0–9.
 * - Must be between [MIN_LENGTH] and [MAX_LENGTH] characters long (excluding the `#` if present).
 *
 * Use [create], [createOrNull], or [createOrThrow] to safely construct a `PlayerTag` instance.
 *
 * This type enforces validity at creation and should be treated as a type-safe representation
 * of a Brawl Stars tag. Raw strings should not be passed around where [PlayerTag] is expected.
 */
@JvmInline
value class PlayerTag private constructor(
    /** The validated tag string, as accepted by the Brawl Stars API. */
    private val rawString: String,
) {
    /** Constants with constraints and validation */
    companion object {
        /** The minimum number of characters in a tag (excluding the optional `#`). */
        const val MIN_LENGTH: Int = 3

        /** The maximum number of characters in a tag (excluding the optional `#`). */
        const val MAX_LENGTH: Int = 16

        /**
         * Regular expression to match a valid player tag.
         *
         * Accepts optional leading `#` and ensures uppercase alphanumeric content
         * of valid length. Matching is case-insensitive for convenience.
         */
        val REGEX: Regex = Regex(
            pattern = "^#?[A-Z0-9]{$MIN_LENGTH,$MAX_LENGTH}$",
            option = RegexOption.IGNORE_CASE,
        )

        /**
         * Returns `true` if the given [input] string is a valid Brawl Stars player tag.
         *
         * Validation is performed using [REGEX] and is case-insensitive.
         */
        fun isValid(input: String): Boolean =
            REGEX.matches(input)

        /**
         * Attempts to create a [PlayerTag] from the given [input].
         *
         * Returns a [Result.success] if valid, or a [Result.failure] containing an [IllegalArgumentException]
         * if invalid.
         */
        fun create(input: String): Result<PlayerTag> =
            if (isValid(input)) Result.success(PlayerTag(input.uppercase()))
            else Result.failure(IllegalArgumentException("Invalid tag: $input"))

        /**
         * Creates a [PlayerTag] from [input] or throws [IllegalArgumentException] if the input is invalid.
         *
         * This is a convenience wrapper around [create].
         */
        fun createOrThrow(input: String): PlayerTag =
            create(input).getOrThrow()

        /**
         * Creates a [PlayerTag] from [input], or returns `null` if the input is invalid.
         *
         * This is a convenience wrapper around [create].
         */
        fun createOrNull(input: String): PlayerTag? =
            create(input).getOrNull()
    }

    /**
     * Returns the tag string with a leading `#`, regardless of whether the original input had one.
     *
     * This is useful when formatting for display or when making API requests that require the `#` prefix.
     */
    val stringWithTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString else "#$rawString"

    /**
     * Returns the tag string without the leading `#`, if present.
     *
     * This is useful when storing or comparing raw tag values.
     */
    val stringWithoutTagPrefix: String
        get() = if (rawString.startsWith("#")) rawString.substring(1) else rawString

    /**
     * Returns the canonical string representation of this player tag, always prefixed with `#`.
     */
    override fun toString(): String = stringWithTagPrefix
}

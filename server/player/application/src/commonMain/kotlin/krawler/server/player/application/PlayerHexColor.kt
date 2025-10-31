package krawler.server.player.application

import krawler.server.player.application.PlayerHexColor.Companion.FORMAT

/**
 * Represents the hexadecimal color assigned to a player's name in Brawl Stars.
 *
 * The color is typically displayed in the player's profile or club list. It is defined
 * using a 3- or 6-digit hexadecimal string, optionally prefixed with `#` (e.g. `#FF0000` or `FFF`).
 *
 * @property rawString The raw hexadecimal string representing the color, possibly with a leading `#`.
 */
@JvmInline
value class PlayerHexColor private constructor(
    val rawString: String,
) {
    companion object {
        /**
         * Regex used to validate a valid color string (with optional `#`).
         */
        val FORMAT: Regex = Regex("^#?(?:[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")

        /**
         * Checks whether the given color string is valid.
         *
         * @param value The raw string to check.
         * @return `true` if it matches the [FORMAT], `false` otherwise.
         */
        fun isValid(value: String): Boolean = FORMAT.matches(value)

        /**
         * Creates a [PlayerHexColor] instance if the provided [value] is valid.
         *
         * @param value The raw string to validate and wrap.
         * @return [Result.success] with a [PlayerHexColor], or [Result.failure] if invalid.
         */
        fun create(value: String): Result<PlayerHexColor> =
            if (isValid(value)) Result.success(PlayerHexColor(value))
            else Result.failure(IllegalArgumentException("Invalid HexColor: '$value'"))

        /**
         * Creates a [PlayerHexColor] or returns `null` if the string is invalid.
         *
         * @param value The raw string to validate and wrap.
         * @return A [PlayerHexColor] or `null`.
         */
        fun createOrNull(value: String): PlayerHexColor? =
            create(value).getOrNull()

        /**
         * Creates a [PlayerHexColor] or throws [IllegalArgumentException] if the string is invalid.
         *
         * @param value The raw string to validate and wrap.
         * @return A [PlayerHexColor] instance.
         * @throws IllegalArgumentException if [value] is not valid.
         */
        fun createOrThrow(value: String): PlayerHexColor =
            create(value).getOrThrow()
    }
}

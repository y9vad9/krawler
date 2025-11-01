package krawler.server.player.application

/**
 * Represents the hexadecimal color assigned to a player's name in Brawl Stars.
 *
 * The color is typically displayed in the player's profile or club list. It is defined
 * using a 3- or 6-digit hexadecimal string, optionally prefixed with `#` (e.g. `#FF0000` or `FFF`).
 *
 * @property rawString The raw hexadecimal string representing the color, possibly with a leading `#`.
 */
@JvmInline
value class PlayerHexColor(
    val rawString: String,
) {
    init {
        require(rawString.length <= MAX_LENGTH) {
            "Invalid HexColor: '$rawString'. Must be at most 9 characters long (including optional '#')."
        }

        require(FORMAT.matches(rawString)) {
            "Invalid HexColor format: '$rawString'. Must be 3 or 6 hexadecimal digits, optionally prefixed with '#'."
        }
    }

    companion object {
        /**
         * Regex used to validate a valid color string (with optional `#`).
         */
        val FORMAT: Regex = Regex("^#?(?:[A-Fa-f0-9]{3}|[A-Fa-f0-9]{6})$")

        /**
         * Max length of the string containing player's hex color.
         */
        const val MAX_LENGTH: Int = 9
    }

    /**
     * Returns the canonical string representation of the color.
     */
    override fun toString(): String = rawString
}

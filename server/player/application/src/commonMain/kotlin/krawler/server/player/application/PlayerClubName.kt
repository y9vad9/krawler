package krawler.server.player.application

import krawler.server.player.application.PlayerClubName.Companion.MAX_LENGTH
import krawler.server.player.application.PlayerClubName.Companion.MIN_LENGTH

/**
 * Represents a Brawl Stars club name, as displayed in-game.
 *
 * A valid club name:
 * - Must be at least [MIN_LENGTH] character long.
 * - Must not exceed [MAX_LENGTH] characters.
 * - Can contain any characters, including emojis and symbols.
 *
 * @property rawString The validated club name string.
 */
@JvmInline
value class PlayerClubName(
    val rawString: String
) : Comparable<PlayerClubName> {

    init {
        require(rawString.length in MIN_LENGTH..MAX_LENGTH) {
            "Club name length must be between $MIN_LENGTH and $MAX_LENGTH characters: $rawString"
        }
    }

    /**
     * Compares this [PlayerClubName] with another by their string values.
     */
    override fun compareTo(other: PlayerClubName): Int =
        rawString.compareTo(other.rawString)

    /**
     * Returns the raw string representation of the club name.
     */
    override fun toString(): String = rawString

    companion object {
        /** The minimum allowed length of a club name. */
        const val MIN_LENGTH: Int = 1

        /** The maximum allowed length of a club name. */
        const val MAX_LENGTH: Int = 20
    }
}

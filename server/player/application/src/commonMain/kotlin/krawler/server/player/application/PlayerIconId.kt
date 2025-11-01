package krawler.server.player.application

/**
 * Represents a unique identifier for a player icon in Brawl Stars.
 *
 * Player icon IDs are integer values used to reference avatar icons in the Brawl Stars API.
 * These IDs typically start at **28000000** and increase from there, corresponding to visual
 * assets in the game client.
 *
 * This class is comparable based on its [raw] integer value.
 *
 * @property raw The raw integer ID of the player icon.
 */
@JvmInline
value class PlayerIconId(
    val raw: Int
) : Comparable<PlayerIconId> {

    init {
        require(raw >= MIN_VALUE) {
            "PlayerIconId must be greater than or equal to $MIN_VALUE, but was $raw."
        }
    }

    /**
     * Compares this [PlayerIconId] with another by their [raw] values.
     */
    override fun compareTo(other: PlayerIconId): Int = raw.compareTo(other.raw)

    /**
     * Returns the string representation of this player icon ID.
     */
    override fun toString(): String = raw.toString()

    companion object {
        /** Minimum allowed ID for a valid player icon. */
        const val MIN_VALUE: Int = 28_000_000
    }
}

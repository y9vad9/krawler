package krawler.server.player.application

/**
 * Represents a player's current and highest trophy counts.
 *
 * The current count reflects the player's live progress. The highest count
 * is the peak ever reached. The current count must not exceed the highest.
 *
 * @property current Current number of trophies.
 * @property highest All-time highest number of trophies.
 */
data class PlayerTrophies(
    val current: Trophies,
    val highest: Trophies,
) {
    init {
        require(current <= highest) {
            "Player highest can't be less than current."
        }
    }
}

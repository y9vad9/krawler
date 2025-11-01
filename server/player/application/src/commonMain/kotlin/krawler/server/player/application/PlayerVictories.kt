package krawler.server.player.application

/**
 * Represents the number of victories a player has achieved across different battle types in Brawl Stars.
 *
 * These stats are often visible on a player's public profile and are a reflection
 * of their long-term performance across various match formats.
 *
 * @property solo Number of victories in solo Showdown matches.
 * @property duo Number of victories in duo Showdown matches.
 * @property trio Number of victories in 3v3 game modes (e.g., Gem Grab, Brawl Ball, Hot Zone).
 */
data class PlayerVictories(
    val solo: VictoryAmount,
    val duo: VictoryAmount,
    val trio: VictoryAmount,
) {

    /**
     * Returns total amount of victories among solo, duo and trio game modes. This
     * does not include 5-vs-5 victories, because Brawl Stars API itself does not support it.
     */
    val total: VictoryAmount get() = solo + duo + trio
}

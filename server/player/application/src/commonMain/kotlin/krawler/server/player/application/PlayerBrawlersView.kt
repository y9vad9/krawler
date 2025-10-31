package krawler.server.player.application

/**
 * Represents a summarized view of a player's brawlers.
 *
 * Contains the total number of brawlers the player owns, along with
 * the player's favorite brawler and the brawler with the best performance.
 *
 * @property amount Total number of brawlers the player owns.
 * @property favorite The player's favorite brawler.
 * @property best The player's best-performing brawler.
 */
data class PlayerBrawlersView(
    val amount: BrawlersAmount,
    val favorite: PlayerBrawler,
    val best: PlayerBrawler,
)

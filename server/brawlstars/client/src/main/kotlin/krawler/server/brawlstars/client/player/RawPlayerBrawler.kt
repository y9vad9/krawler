package krawler.server.brawlstars.client.player

import kotlinx.serialization.Serializable
import krawler.server.brawlstars.client.brawler.RawBrawlerGadget
import krawler.server.brawlstars.client.brawler.RawBrawlerStarPower

/**
 * Represents a player's specific brawler data in Brawl Stars.
 *
 * This includes the brawler's identity, progression stats, and equipped
 * abilities and items.
 *
 * @property id The unique identifier of the brawler.
 * @property name The display name of the brawler.
 * @property power The current power level of the brawler.
 * @property rank The rank of the brawler, indicating progression.
 * @property trophies The number of trophies currently held with this brawler.
 * @property highestTrophies The highest trophy count ever achieved with this brawler.
 * @property gears The list of gears the player has equipped on this brawler.
 * @property starPowers The list of star powers unlocked and available for this brawler.
 * @property gadgets The list of gadgets unlocked and available for this brawler.
 * @property currentWinStreak Current amount of victories achieved by this brawler in a row.
 * @property maxWinStreak Max amount of victories achieved by this brawler in a row.
 */
@Serializable
data class RawPlayerBrawler(
    val id: Int,
    val name: String,
    val power: Int,
    val rank: Int,
    val trophies: Int,
    val highestTrophies: Int,
    val gears: List<RawBrawlerGear>,
    val starPowers: List<RawBrawlerStarPower>,
    val gadgets: List<RawBrawlerGadget>,
    val currentWinStreak: Int? = null,
    val maxWinStreak: Int? = null,
)

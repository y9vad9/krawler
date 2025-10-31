package krawler.server.player.application

import krawler.server.player.application.brawler.BrawlerGadget
import krawler.server.player.application.brawler.BrawlerGear
import krawler.server.player.application.brawler.BrawlerId
import krawler.server.player.application.brawler.BrawlerName
import krawler.server.player.application.brawler.BrawlerPowerLevel
import krawler.server.player.application.brawler.BrawlerRank
import krawler.server.player.application.brawler.BrawlerStarPower

/**
 * Represents a Brawler in Brawl Stars.
 *
 * Contains all relevant data about a specific brawler, including identifiers,
 * progression stats, and available equipment or abilities.
 *
 * @property id The unique identifier of the brawler.
 * @property name The display name of the brawler.
 * @property level The current power level of the brawler.
 * @property rank The current rank of the brawler.
 * @property trophies The trophy counts associated with this brawler.
 * @property gadgets The list of gadgets unlocked or equipped by the brawler.
 * @property gears The list of gears the brawler has equipped.
 * @property starPowers The list of star powers unlocked for the brawler.
 */
data class PlayerBrawler(
    val id: BrawlerId,
    val name: BrawlerName,
    val level: BrawlerPowerLevel,
    val rank: BrawlerRank,
    val trophies: Trophies,
    val gadgets: List<BrawlerGadget>,
    val gears: List<BrawlerGear>,
    val starPowers: List<BrawlerStarPower>,
)


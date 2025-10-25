package krawler.server.brawlstars.client.player

import kotlinx.serialization.Serializable

/**
 * Represents a gear item equipped by a player on a brawler.
 *
 * Gears grant special abilities or enhancements that modify the brawler’s performance
 * in battle. Although the `level` property currently appears to always be `3` due to
 * the removal of the gear upgrade mechanic, it is retained for compatibility and potential
 * future use.
 *
 * @property id The unique identifier of the gear.
 * @property name The human-readable name of the gear.
 * @property level The current gear level, typically fixed at 3 following the removal
 * of gear upgrades.
 */
@Serializable
data class RawBrawlerGear(
    val id: Int,
    val name: String,
    val level: Int
)

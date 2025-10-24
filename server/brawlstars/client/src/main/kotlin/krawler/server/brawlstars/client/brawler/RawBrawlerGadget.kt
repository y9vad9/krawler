package krawler.server.brawlstars.client.brawler

import kotlinx.serialization.Serializable

/**
 * Represents a Gadget equipped by a brawler.
 *
 * @property id The unique numeric identifier for the Gadget (e.g., 23_000_000).
 * @property name The display name of the Gadget as shown in-game.
 */
@Serializable
data class RawBrawlerGadget(
    val id: Int,
    val name: String
)

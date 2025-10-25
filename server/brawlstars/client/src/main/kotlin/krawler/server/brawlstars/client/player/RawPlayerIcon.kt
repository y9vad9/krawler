package krawler.server.brawlstars.client.player

import kotlinx.serialization.Serializable

/**
 * Represents a player's icon in Brawl Stars.
 *
 * Icons are visual badges players can display alongside their profile.
 *
 * @property id The unique identifier of the icon.
 */
@Serializable
data class RawPlayerIcon(
    val id: Int,
)

package krawler.server.brawlstars.client.brawler

import kotlinx.serialization.Serializable

/**
 * Represents a brawler's Star Power.
 *
 * @property id The numeric ID of the Star Power (e.g., 23000000).
 * @property name The human-readable name of the Star Power.
 */
@Serializable
data class RawBrawlerStarPower(
    val id: Int,
    val name: String
)

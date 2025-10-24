package krawler.server.brawlstars.client.player

import kotlinx.serialization.Serializable

/**
 * Represents a player's club affiliation in Brawl Stars.
 *
 * Contains basic information about the club the player belongs to.
 *
 * @property tag The unique club tag identifier, used for referencing the club.
 * @property name The display name of the club.
 */
@Serializable
data class RawPlayerClub(
    val tag: String? = null,
    val name: String? = null,
)

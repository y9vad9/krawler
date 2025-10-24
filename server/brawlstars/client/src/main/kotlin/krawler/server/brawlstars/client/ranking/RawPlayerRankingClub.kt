package krawler.server.brawlstars.client.ranking

import kotlinx.serialization.Serializable

/**
 * Represents a club associated with a player in ranking data.
 *
 * @property name The name of the club.
 */
@Serializable
data class RawPlayerRankingClub(
    val name: String,
)

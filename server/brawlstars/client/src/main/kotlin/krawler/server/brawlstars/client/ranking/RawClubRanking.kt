package krawler.server.brawlstars.client.ranking

import kotlinx.serialization.Serializable

/**
 * Represents a club's ranking information in leaderboards.
 *
 * @property tag The unique tag identifying the club.
 * @property name The display name of the club.
 * @property trophies The total number of trophies earned by the club.
 * @property badgeId The identifier for the club's badge icon.
 * @property rank The club's position in the ranking.
 * @property memberCount The number of members that joined the club.
 */
@Serializable
data class RawClubRanking(
    val tag: String,
    val name: String,
    val trophies: Int,
    val badgeId: Int,
    val rank: Int,
    val memberCount: Int,
)

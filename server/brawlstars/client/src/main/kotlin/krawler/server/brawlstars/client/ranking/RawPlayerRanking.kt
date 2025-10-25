package krawler.server.brawlstars.client.ranking

import kotlinx.serialization.Serializable
import krawler.server.brawlstars.client.player.RawPlayerIcon

/**
 * Represents a player's ranking information in leaderboards.
 *
 * @property trophies The number of trophies the player has.
 * @property club The club to which the player belongs.
 * @property icon The player's icon information.
 * @property tag The unique player tag.
 * @property name The display name of the player.
 * @property rank The player's position in the ranking.
 * @property nameColor The color code for the player's name.
 */
@Serializable
data class RawPlayerRanking(
    val trophies: Int,
    val club: RawPlayerRankingClub? = null,
    val icon: RawPlayerIcon,
    val tag: String,
    val name: String,
    val rank: Int,
    val nameColor: String
)

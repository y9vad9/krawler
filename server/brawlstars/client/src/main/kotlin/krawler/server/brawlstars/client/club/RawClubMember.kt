package krawler.server.brawlstars.client.club

import kotlinx.serialization.Serializable
import krawler.server.brawlstars.client.player.RawPlayerIcon

/**
 * Represents a member of a club.
 *
 * @property icon The player's icon.
 * @property tag The unique club tag of the member.
 * @property name The display name of the member.
 * @property trophies The number of trophies the member has.
 * @property role The member's role in the club (e.g., "Leader", "Member").
 * @property nameColor The display color of the member's name.
 */
@Serializable
data class RawClubMember(
    val icon: RawPlayerIcon,
    val tag: String,
    val name: String,
    val trophies: Int,
    val role: String,
    val nameColor: String,
)

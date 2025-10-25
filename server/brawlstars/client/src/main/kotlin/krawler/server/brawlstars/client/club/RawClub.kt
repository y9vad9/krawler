package krawler.server.brawlstars.client.club

import kotlinx.serialization.Serializable

/**
 * Represents a Brawl Stars club as returned by the official API.
 *
 * @property tag Unique tag identifier of the club (e.g., "#ABC123").
 * @property name Display name of the club.
 * @property description Free-form description written by the club’s leader.
 * @property trophies Total number of trophies accumulated by all club members.
 * @property requiredTrophies Minimum number of trophies a player must have to join the club.
 * @property members List of current members in the club.
 * @property type Join type of the club (e.g., "open", "inviteOnly", "closed").
 * @property badgeId Identifier of the club’s badge (used for visual representation).
 */
@Serializable
data class RawClub(
    val tag: String,
    val name: String,
    val description: String,
    val trophies: Int,
    val requiredTrophies: Int,
    val members: List<RawClubMember>,
    val type: String,
    val badgeId: Int,
)

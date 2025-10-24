package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Generic participant representation used in both `players` and `teams` structures.
 *
 * @property tag The player's unique identifier, nullable for bots or special cases.
 * @property name The player's display name, nullable.
 * @property brawler A single brawler info object associated with the participant, nullable.
 * @property brawlers A list of brawler info objects when multiple brawlers are involved, nullable.
 */
@Serializable
data class RawBattleParticipant(
    val tag: String? = null,
    val name: String? = null,
    val brawler: RawBattleBrawler? = null,
    val brawlers: List<RawBattleBrawler>? = null,
)

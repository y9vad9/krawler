package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Represents a single battle record as returned by the API.
 *
 * @property battleTime The timestamp of when the battle occurred, as a raw string.
 * @property event The event metadata associated with this battle.
 * @property battle The detailed battle data including participants, results, and other info.
 */
@Serializable
data class RawBattleRecord(
    val battleTime: String,
    val event: RawBattleEvent,
    val battle: RawBattleDetails,
)

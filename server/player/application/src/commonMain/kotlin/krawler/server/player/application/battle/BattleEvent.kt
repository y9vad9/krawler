package krawler.server.player.application.battle

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents a Brawl Stars event context in battle logs.
 *
 * An event refers to the origin of the map and mode in a match:
 * - [OfficialBattleEvent]: Played on an official Supercell map in a public queue.
 * - [MapMakerBattleEvent]: Played on a community-created map, either in matchmaking
 *   (e.g., Candidates/Winners of the Day) or in a friendly room.
 *
 * These types appear in match metadata but may carry varying levels of detail,
 * depending on their source and API availability.
 */
sealed interface BattleEvent {
    /**
     * The mode of the event. E.g: Brawl Ball, Solo Showdown.
     */
    val mode: BattleEventMode
}

/**
 * Returns `true` if this [BattleEvent] is an [OfficialBattleEvent], which represents a
 * standard match played on an official Supercell map.
 */
@OptIn(ExperimentalContracts::class)
fun BattleEvent.isOfficial(): Boolean {
    contract {
        returns(true) implies (this@isOfficial is OfficialBattleEvent)
    }
    return this is OfficialBattleEvent
}

/**
 * Returns `true` if this [BattleEvent] is a [MapMakerBattleEvent], which represents
 * a custom map event, either in matchmaking or a friendly room.
 */
@OptIn(ExperimentalContracts::class)
fun BattleEvent.isMapMaker(): Boolean {
    contract {
        returns(true) implies (this@isMapMaker is MapMakerBattleEvent)
    }
    return this is MapMakerBattleEvent
}

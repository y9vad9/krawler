package krawler.server.player.application.battle

/**
 * The standard public event from the Brawl Stars battle log.
 *
 * These events are part of official matchmaking and played on Supercell-defined maps
 * and modes, such as Gem Grab or Brawl Ball. This type includes full metadata.
 *
 * @property id Unique event identifier provided by the Brawl Stars API.
 * @property mapName Name of the map used in the match.
 * @property mode Game mode (e.g., Heist, Bounty, Hot Zone).
 */
data class OfficialBattleEvent(
    val id: BattleEventId,
    val mapName: BattleMapName,
    override val mode: BattleEventMode,
) : BattleEvent

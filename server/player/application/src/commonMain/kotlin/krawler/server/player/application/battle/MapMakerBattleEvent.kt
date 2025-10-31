package krawler.server.player.application.battle

/**
 * Represents a mapmaker event in both matchmaking and friendly rooms.
 *
 * This includes:
 * - Candidates of the Day / Winners of the Day (public matchmaking)
 * - Friendly matches using custom community maps
 *
 * The Brawl Stars API provides no reliable metadata for these events,
 * so the only information we carry here is [mode] – it's obtained indirectly from the battle itself.
 */
data class MapMakerBattleEvent(override val mode: BattleEventMode) : BattleEvent

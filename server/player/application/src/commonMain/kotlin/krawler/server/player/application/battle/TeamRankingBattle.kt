package krawler.server.player.application.battle

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Instant

/**
 * Represents a [RankingBattle] that involves multiple teams.
 *
 * This sealed interface defines a shared contract for all ranking-based battles
 * that are structured around team participation.
 */
sealed interface TeamRankingBattle : RankingBattle, TeamBattle {
    /** The teams that participated in the battle. */
    val teams: BattleTeamsParticipants
}

/**
 * Marker interface for **friendly team ranking battles**.
 *
 * These are unranked team battles played in a friendly room. They inherit
 * ranking behavior from [TeamRankingBattle] but use [FriendlyBattleTeamsParticipants]
 * to represent the teams.
 *
 * Friendly team ranking battles do **not affect trophies or official rankings**.
 */
sealed interface FriendlyTeamRankingBattle : TeamRankingBattle, FriendlyRankingBattle {
    override val teams: FriendlyBattleTeamsParticipants
}

/**
 * Marker interface for **team ranking battles on Map Maker maps**.
 *
 * These battles inherit ranking behavior from [TeamRankingBattle] and carry
 * [MapMakerBattleEvent] information through [MapMakerRankingBattle].
 *
 * Currently used for community-created maps.
 */
sealed interface MapMakerTeamRankingBattle : TeamRankingBattle, MapMakerRankingBattle

/**
 * A team-based [TeamRankingBattle] that took place in a friendly match context.
 *
 * This battle does not affect trophy progression and is often used for practice or casual play.
 *
 * @property time The timestamp of when the battle occurred.
 * @property rank The player's position in the battle ranking.
 * @property event The [BattleEvent] during which the battle took place.
 * @property teams The teams that participated in the battle.
 */
data class ClassicFriendlyTeamRankingBattle(
    override val time: Instant,
    override val rank: RankingPosition,
    override val event: BattleEvent,
    override val teams: FriendlyBattleTeamsParticipants,
) : FriendlyTeamRankingBattle

/**
 * A team-based [TeamRankingBattle] that took place in a friendly match context.
 *
 * This battle does not affect trophy progression and is often used for practice or casual play.
 *
 * @property time The timestamp of when the battle occurred.
 * @property rank The player's position in the battle ranking.
 * @property event The [BattleEvent] during which the battle took place.
 * @property teams The teams that participated in the battle.
 */
data class FriendlyMapMakerTeamRankingBattle(
    override val time: Instant,
    override val rank: RankingPosition,
    override val event: MapMakerBattleEvent,
    override val teams: FriendlyBattleTeamsParticipants,
) : FriendlyTeamRankingBattle, MapMakerTeamRankingBattle

/**
 * A team-based [TeamRankingBattle] that took place in a friendly match context.
 *
 * This battle does not affect trophy progression and is often used for practice or casual play.
 *
 * @property time The timestamp of when the battle occurred.
 * @property rank The player's position in the battle ranking.
 * @property event The [BattleEvent] during which the battle took place.
 * @property teams The teams that participated in the battle.
 */
data class GlobalMapMakerTeamRankingBattle(
    override val time: Instant,
    override val rank: RankingPosition,
    override val event: MapMakerBattleEvent,
    override val teams: TrophyLeagueBattleTeamsPlayers,
) : MapMakerTeamRankingBattle

/**
 * A team-based [RankingBattle] that occurred as part of the trophy league.
 *
 * This battle contributes to the player's trophy progress and is part of the core competitive gameplay.
 *
 * @property time The timestamp of when the battle occurred.
 * @property rank The player's position in the battle ranking.
 * @property event The [BattleEvent] during which the battle took place.
 * @property teams The teams that participated in the battle.
 * @property trophyChange How much trophies were subtracted/earned by the player.
 */
data class TrophyLeagueTeamRankingBattle(
    override val time: Instant,
    override val rank: RankingPosition,
    override val event: BattleEvent,
    override val teams: TrophyLeagueBattleTeamsPlayers,
    override val trophyChange: TrophyChange,
) : TeamRankingBattle, TrophyLeagueRankingBattle

/**
 * Returns `true` if this [TeamRankingBattle] is a [FriendlyBattle], allowing smart casting.
 */
@OptIn(ExperimentalContracts::class)
fun TeamRankingBattle.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is ClassicFriendlyTeamRankingBattle)
    }
    return this is ClassicFriendlyTeamRankingBattle
}

/**
 * Returns `true` if this [TeamRankingBattle] is a [TrophyLeagueTeamRankingBattle], allowing smart casting.
 */
@OptIn(ExperimentalContracts::class)
fun TeamRankingBattle.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueTeamRankingBattle)
    }
    return this is TrophyLeagueTeamRankingBattle
}

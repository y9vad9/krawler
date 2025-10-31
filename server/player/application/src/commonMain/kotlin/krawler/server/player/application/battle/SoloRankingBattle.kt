package krawler.server.player.application.battle

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Instant

/**
 * Represents a solo-based ranked battle in Brawl Stars.
 *
 * This interface covers battle types where players are ranked individually,
 * such as Solo Showdown or Duels. It provides access to the participants
 * of the battle along with their brawlers.
 */
sealed interface SoloRankingBattle : RankingBattle, SoloBattle {
    /**
     * The list of participant that can be either bot (if battle is friendly) or
     * a real player.
     */
    val participants: BattleParticipants
}

/**
 * Marker interface for **solo ranking battles** that occur on **Map Maker maps**.
 *
 * These battles inherit ranking properties from [SoloRankingBattle] and
 * carry [MapMakerBattleEvent] information through [MapMakerRankingBattle].
 */
sealed interface MapMakerSoloRankingBattle : SoloRankingBattle, MapMakerRankingBattle

/**
 * Marker interface for **friendly solo ranking battles**.
 *
 * These battles are unranked, 1v1 matches in friendly rooms and do not affect trophies.
 * They inherit ranking properties from [SoloRankingBattle] but use [FriendlyBattleParticipants]
 * to represent the participants.
 */
sealed interface FriendlySoloRankingBattle : SoloRankingBattle, FriendlyRankingBattle {
    override val participants: FriendlyBattleParticipants
}

/**
 * Represents a solo ranked battle in the Friendly game mode.
 *
 * Friendly battles do not affect player rankings or trophies.
 */
data class ClassicFriendlySoloRankingBattle(
    override val time: Instant,
    override val participants: FriendlyBattleParticipants,
    override val rank: RankingPosition,
    override val event: OfficialBattleEvent,
) : FriendlySoloRankingBattle

/**
 * Represents a solo battle on a Map Maker map in the Friendly game mode.
 */
data class FriendlyMapMakerSoloRankingBattle(
    override val time: Instant,
    override val participants: FriendlyBattleParticipants,
    override val rank: RankingPosition,
    override val event: MapMakerBattleEvent,
) : FriendlySoloRankingBattle, MapMakerSoloRankingBattle

/**
 * Represents a solo battle on a Map Maker map in the Friendly game mode.
 */
data class GlobalMapMakerSoloRankingBattle(
    override val time: Instant,
    override val participants: TrophyLeagueBattlePlayers,
    override val rank: RankingPosition,
    override val event: MapMakerBattleEvent,
) : MapMakerSoloRankingBattle {
    /**
     * An alias to [participants].
     */
    inline val players: TrophyLeagueBattlePlayers get() = participants
}

/**
 * Represents a solo ranked battle in the Trophy League game mode.
 */
data class TrophyLeagueSoloRankingBattle(
    override val time: Instant,
    override val participants: TrophyLeagueBattlePlayers,
    override val rank: RankingPosition,
    override val event: BattleEvent,
    override val trophyChange: TrophyChange,
) : SoloRankingBattle, TrophyLeagueRankingBattle {
    /**
     * An alias to [participants].
     */
    inline val players: TrophyLeagueBattlePlayers get() = participants
}

/**
 * Returns `true` if this [SoloRankingBattle] is a friendly battle,
 * allowing smart casting to [ClassicFriendlySoloRankingBattle].
 *
 * This function provides Kotlin contract support to enable smart casts after
 * successful checks.
 */
@OptIn(ExperimentalContracts::class)
fun SoloRankingBattle.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is ClassicFriendlySoloRankingBattle)
    }
    return this is ClassicFriendlySoloRankingBattle
}

/**
 * Returns `true` if this [SoloRankingBattle] is a trophy league battle,
 * allowing smart casting to [TrophyLeagueSoloRankingBattle].
 *
 * This function provides Kotlin contract support to enable smart casts after
 * successful checks.
 */
@OptIn(ExperimentalContracts::class)
fun SoloRankingBattle.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueSoloRankingBattle)
    }
    return this is TrophyLeagueSoloRankingBattle
}

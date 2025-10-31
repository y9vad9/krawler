package krawler.server.player.application.battle

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Represents a ranked battle in Brawl Stars.
 *
 * Learn more on [Brawl Stars Fandom](https://brawlstars.fandom.com/wiki/Ranked).
 */
sealed interface RankedBattle : TeamVsTeamBattle {
    /**
     * The list of battle rounds (inferred or actual).
     */
    val rounds: List<Round>

    /**
     * The result of the battle.
     *
     * Might be null if the game is unfinished (can be either in progress or just left through the exit button
     * in case of friendly battles).
     */
    override val result: BattleResult?

    /**
     * The star player of the battle.
     */
    override val starPlayer: BattleStarPlayer

    /**
     * The matchmaking type (solo/duo/trio).
     */
    val matchmakingType: RankedMatchmakingType

    /**
     * Represents a single round within a battle.
     *
     * A round contains the outcome (win/draw/loss) and its duration.
     *
     * @property result The outcome of the round for the team, represented by [BattleResult].
     * @property duration The time the round lasted, as a [Duration].
     */
    data class Round(
        val result: BattleResult,
        val duration: Duration,
    )
}

/**
 * Interface-marker for the friendly ranked battles.
 */
sealed interface FriendlyRankedBattle : RankedBattle, FriendlyTeamVsTeamBattle {
    override val firstTeam: FriendlyBattleParticipants
    override val secondTeam: FriendlyBattleParticipants
}

/**
 * Returns true if this ranked battle has a recorded result.
 */
val RankedBattle.isFinished: Boolean get() = result != null

/**
 * Computes the total duration of the ranked battle by summing round durations.
 */
val RankedBattle.totalDuration: Duration
    get() = rounds.fold(0.seconds) { acc, round ->
        acc + round.duration
    }

/**
 * Represents a friendly ranked game battle, which may be assumed based on incomplete data.
 *
 * Any friendly ranked game battle is **ASSUMED**, meaning that there's no
 * actual indicator on the Brawl Stars API side – it is inferred by other battles.
 *
 * It's for the most part reliable data, excluding very exclusive cases (when you're directly
 * trying to break the algorithm, but even in this situation, most known cases
 * are covered).
 *
 * ## How it's inferred?
 * Friendly ranked game battles are inferred by the following rules:
 * - `battle.mode` is playable ranked game (showdown, for example, is excluded)
 * - Every ranked game has last game with `battle.result == victory || defeat`
 *
 * ### How rounds are inferred?
 * Although Brawl Stars itself cannot display friendly ranked matches as one even within the game,
 * we can infer it by following rules:
 * - First game, as was mentioned before, should be victory or defeat (no draw can be, it will be finished at least by
 * bots, or it will be a specially made long-ride match).
 * - 'Battles' below should be without `starPlayer` defined (null) – it's the rule, because as was mentioned
 * there cannot be any draws or games without actions.
 * - 'Battles' events, teams, mode should be equal and its time should within a reasonable timeframe.
 *
 * ## Known limitations
 * - If it's the last battle can be obtained log, it will be marked as regular [TeamBattle].
 * - If there's no final round, all rounds until then will be marked as [TeamBattle].
 * - I assume that after some period of time ranked battle can be finished by force by Brawl Stars itself,
 * so better to check [rounds] count and overall [totalDuration] with some reasonable time. At our side
 * we set a 25-minute limit for friendly matches, but you may validate it yourself.
 */
data class ClassicFriendlyRankedBattle(
    override val time: Instant,
    override val event: BattleEvent,
    override val rounds: List<RankedBattle.Round>,
    override val starPlayer: FriendlyBattleStarPlayer,
    override val result: BattleResult?,
    override val matchmakingType: RankedMatchmakingType,
    override val firstTeam: FriendlyBattleParticipants,
    override val secondTeam: FriendlyBattleParticipants,
) : RankedBattle, FriendlyRankedBattle

/**
 * Represents a friendly ranked game battle with map from a Map Maker, which may be assumed based on incomplete data.
 *
 * > **Note**: As per today, there's no mapmaker ranked battles available, we keep it for
 * > forward-compatibility purposes.
 *
 * Any friendly ranked game battle is **ASSUMED**, meaning that there's no
 * actual indicator on the Brawl Stars API side – it is inferred by other battles.
 *
 * It's for the most part reliable data, excluding very exclusive cases (when you're directly
 * trying to break the algorithm, but even in this situation, most known cases
 * are covered).
 *
 * ## How it's inferred?
 * Friendly ranked game battles are inferred by the following rules:
 * - `battle.mode` is playable ranked game (showdown, for example, is excluded)
 * - Every ranked game has last game with `battle.result == victory || defeat`
 *
 * ### How rounds are inferred?
 * Although Brawl Stars itself cannot display friendly ranked matches as one even within the game,
 * we can infer it by following rules:
 * - First game, as was mentioned before, should be victory or defeat (no draw can be, it will be finished at least by
 * bots, or it will be a specially made long-ride match).
 * - 'Battles' below should be without `starPlayer` defined (null) – it's the rule, because as was mentioned
 * there cannot be any draws or games without actions.
 * - 'Battles' events, teams, mode should be equal and its time should within a reasonable timeframe.
 *
 * ## Known limitations
 * - If it's the last battle can be obtained log, it will be marked as regular [TeamBattle].
 * - If there's no final round, all rounds until then will be marked as [TeamBattle].
 * - I assume that after some period of time ranked battle can be finished by force by Brawl Stars itself,
 * so better to check [rounds] count and overall [totalDuration] with some reasonable time. At our side
 * we set a 25-minute limit for friendly matches, but you may validate it yourself.
 */
data class FriendlyMapMakerRankedBattle(
    override val time: Instant,
    override val event: MapMakerBattleEvent,
    override val rounds: List<RankedBattle.Round>,
    override val starPlayer: FriendlyBattleStarPlayer,
    override val result: BattleResult?,
    override val matchmakingType: RankedMatchmakingType,
    override val firstTeam: FriendlyBattleParticipants,
    override val secondTeam: FriendlyBattleParticipants,
) : RankedBattle, FriendlyRankedBattle, MapMakerTeamVsTeamBattle

/**
 * Represents a regular ranked game battle with explicitly recorded data.
 *
 * ## How it's inferred?
 * Apart from the straight-forward way that might tell us that particular
 * battle is ranked – some of them might be just 'rounds' for them,
 * meaning that they are not that important for the full picture.
 *
 * In the same way as [ClassicFriendlyRankedBattle] infers, we just
 * look for a previous battles from the last one with specified 'starPlayer'
 * in API (in ranked rounds there's no defined starPlayer) and then down the list
 * while there's the same mode, event, teams, map and no 'starPlayer' event.
 */
data class RankedLeagueBattle(
    override val time: Instant,
    override val event: OfficialBattleEvent,
    override val rounds: List<RankedBattle.Round>,
    override val firstTeam: RankedBattlePlayers,
    override val secondTeam: RankedBattlePlayers,
    override val starPlayer: RankedBattleStarPlayer,
    override val result: BattleResult?,
    override val matchmakingType: RankedMatchmakingType,
) : RankedBattle

/**
 * Returns true if this [RankedBattle] is a [ClassicFriendlyRankedBattle].
 * Supports smart-casting.
 */
@OptIn(ExperimentalContracts::class)
fun RankedBattle.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is FriendlyRankedBattle)
    }
    return this is FriendlyRankedBattle
}

/**
 * Returns true if this [RankedBattle] is a [ClassicFriendlyRankedBattle].
 * Supports smart-casting.
 */
@OptIn(ExperimentalContracts::class)
fun RankedBattle.isFriendlyClassic(): Boolean {
    contract {
        returns(true) implies (this@isFriendlyClassic is ClassicFriendlyRankedBattle)
    }
    return this is ClassicFriendlyRankedBattle
}

/**
 * Returns true if this [FriendlyRankedBattle] is a [ClassicFriendlyRankedBattle].
 */
@OptIn(ExperimentalContracts::class)
fun FriendlyRankedBattle.isClassic(): Boolean {
    contract {
        returns(true) implies (this@isClassic is ClassicFriendlyRankedBattle)
    }
    return this is ClassicFriendlyRankedBattle
}

/**
 * Returns true if this [FriendlyRankedBattle] is a [ClassicFriendlyRankedBattle].
 */
@OptIn(ExperimentalContracts::class)
fun FriendlyRankedBattle.isMapMaker(): Boolean {
    contract {
        returns(true) implies (this@isMapMaker is FriendlyMapMakerRankedBattle)
    }
    return this is FriendlyMapMakerRankedBattle
}

/**
 * Returns true if this [RankedBattle] is a [FriendlyMapMakerRankedBattle].
 */
@OptIn(ExperimentalContracts::class)
fun RankedBattle.isFriendlyMapMaker(): Boolean {
    contract {
        returns(true) implies (this@isFriendlyMapMaker is FriendlyMapMakerRankedBattle)
    }
    return this is FriendlyMapMakerRankedBattle
}

/**
 * Returns true if this [RankedBattle] is a [RankedLeagueBattle].
 * Supports smart-casting.
 */
@OptIn(ExperimentalContracts::class)
fun RankedBattle.isLeague(): Boolean {
    contract {
        returns(true) implies (this@isLeague is RankedLeagueBattle)
    }
    return this is RankedLeagueBattle
}

/**
 * Returns `true` if this battle was queued in solo.
 *
 * @see isSolo
 */
val RankedBattle.isSoloMatchmaking: Boolean
    get() = matchmakingType.isSolo

/**
 * Returns `true` if this battle was queued in lobby of two people.
 *
 * @see isDuo
 */
val RankedBattle.isDuoMatchmaking: Boolean
    get() = matchmakingType.isDuo

/**
 * Returns `true` if this battle was queued in lobby of three people.
 *
 * @see isTrio
 */
val RankedBattle.isTrioMatchmaking: Boolean
    get() = matchmakingType.isTrio

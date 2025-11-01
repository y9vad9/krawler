package krawler.server.player.application.battle

import krawler.server.player.application.DefeatAmount
import krawler.server.player.application.DrawAmount
import krawler.server.player.application.VictoryAmount
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Instant

/**
 * Represents a generic battle in Brawl Stars.
 *
 * All battle types contain a [time] of occurrence and an associated [event].
 */
sealed interface Battle {
    /** The timestamp at which the battle took place. */
    val time: Instant

    /** The in-game event associated with this battle. */
    val event: BattleEvent
}

/**
 * Marker interface for battles that occurred in the Friendly Game mode.
 *
 * Friendly battles include those created through friendly rooms and do not affect trophies or progression.
 */
sealed interface FriendlyBattle : Battle

/**
 * Marker interface for battles that occurs in solo game modes.
 */
sealed interface SoloBattle : Battle

/**
 * Marker interface for battles that occurs in team game modes.
 */
sealed interface TeamBattle : Battle

/**
 * Marker interface for battles that occurs within trophy league.
 */
sealed interface TrophyLeagueBattle : Battle {
    val trophyChange: TrophyChange
}

/**
 * Marker interface for battles that occurs on unofficial community created maps.
 */
sealed interface MapMakerBattle : Battle {
    override val event: MapMakerBattleEvent
}

/**
 * Returns `true` if this [Battle] is a [MapMakerBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isMapMaker(): Boolean {
    contract {
        returns(true) implies (this@isMapMaker is MapMakerBattle)
    }
    return this is MapMakerBattle
}

/**
 * Returns `true` if this [Battle] is a [FriendlyBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is FriendlyBattle)
    }
    return this is FriendlyBattle
}

/**
 * Returns `true` if this [Battle] is a [TrophyLeagueBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueBattle)
    }
    return this is TrophyLeagueBattle
}

/**
 * Returns `true` if this [Battle] is a [CooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isCooperative(): Boolean {
    contract {
        returns(true) implies (this@isCooperative is CooperativeBattle)
    }
    return this is CooperativeBattle
}

/**
 * Returns `true` if this [Battle] is a [CooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isDuel(): Boolean {
    contract {
        returns(true) implies (this@isDuel is DuelBattle)
    }
    return this is DuelBattle
}

/**
 * Returns `true` if this [Battle] is a [TeamBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isTeamBattle(): Boolean {
    contract {
        returns(true) implies (this@isTeamBattle is TeamBattle)
    }
    return this is TeamBattle
}

/**
 * Returns `true` if this [Battle] is a [TeamVsTeamBattle].
 *
 * > **⚠️ Warning**: take into account that [RankedBattle] is a subtype of the [TeamVsTeamBattle],
 * > meaning that if you check exhaustively, make sure to check whether it's a ranked battle first.
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isTeamVsTeamBattle(): Boolean {
    contract {
        returns(true) implies (this@isTeamVsTeamBattle is TeamVsTeamBattle)
    }
    return this is TeamVsTeamBattle
}

/**
 * Returns `true` if this [Battle] is a [RankedBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isRanked(): Boolean {
    contract {
        returns(true) implies (this@isRanked is RankedBattle)
    }
    return this is RankedBattle
}

/**
 * Returns `true` if this [Battle] is a [RankingBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isRanking(): Boolean {
    contract {
        returns(true) implies (this@isRanking is RankingBattle)
    }
    return this is RankingBattle
}

/**
 * Returns `true` if this [Battle] is a [SoloRankingBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isSoloRankingBattle(): Boolean {
    contract {
        returns(true) implies (this@isSoloRankingBattle is SoloRankingBattle)
    }
    return this is SoloRankingBattle
}

/**
 * Returns `true` if this [Battle] is a [TeamRankingBattle].
 */
@OptIn(ExperimentalContracts::class)
fun Battle.isTeamRankingBattle(): Boolean {
    contract {
        returns(true) implies (this@isTeamRankingBattle is TeamRankingBattle)
    }
    return this is TeamRankingBattle
}

/**
 * Determines if this [Battle] is considered a victory for the relevant player or team.
 *
 * The interpretation depends on the type of battle:
 * - Cooperative battles use [BattleResult].
 * - Trophy League battles use [TrophyChange].
 * - Ranking battles compare [RankingPosition] thresholds.
 * - Ranked, Duel, and Team vs Team battles rely on [BattleResult].
 *
 * Take into account that battles such as [RankedBattle] might not be finished
 * at the time of querying, thus it will not fall under [isVictory], [isDefeat]
 * or [isDraw].
 *
 * ⚠️ This API is experimental because the actual in-game behavior may differ
 * depending on the context. For example:
 * - Friendly battles have static and predictable outcomes.
 * - Trophy battles may treat a loss at 0 trophies as neutral.
 *
 * This API may change, be fixed, or removed at any time.
 */
val Battle.isVictory: Boolean
    get() {
        if (isCooperative()) return result.isVictory
        if (isTrophyLeague()) return trophyChange.isGained
        if (isTeamRankingBattle()) return rank <= RankingPosition.SECOND
        if (isSoloRankingBattle()) return rank <= RankingPosition.FOURTH
        if (isRanked()) return result?.isVictory == true
        if (isDuel()) return result.isVictory
        if (isTeamVsTeamBattle()) return result?.isVictory == true
        return false
    }


/**
 * Determines if this [Battle] is considered a defeat for the relevant player or team.
 *
 * The interpretation depends on the type of battle, similar to [isVictory].
 *
 * Take into account that battles such as [RankedBattle] might not be finished
 * at the time of querying, thus it will not fall under [isVictory], [isDefeat]
 * or [isDraw].
 *
 * ⚠️ This API is experimental because the actual in-game behavior may differ
 * depending on context (e.g., trophy thresholds, special modes).
 *
 * This API may change, be fixed, or removed at any time.
 */
val Battle.isDefeat: Boolean
    get() {
        return when (this) {
            is CooperativeBattle -> result.isDefeat
            is TeamRankingBattle -> rank > RankingPosition.THIRD
            is SoloRankingBattle -> rank > RankingPosition.FIFTH
            is RankedBattle -> result?.isDefeat == true
            is DuelBattle -> result.isDefeat
            is TeamVsTeamBattle -> result?.isDefeat == true
            is TrophyLeagueBattle -> return trophyChange.isLost
        }
    }


/**
 * Determines if this [Battle] is considered a draw or neutral outcome.
 *
 * This includes cases where the player neither won nor lost:
 * - Cooperative battles rely on [BattleResult] with explicit draw if applicable.
 * - Trophy League battles use [TrophyChange.isUnchanged] that can be either 'draw',
 * underdog compensation or the trophies amount is nondeductible (like at 1000 trophies or at 0).
 * - Ranking battles evaluate specific [RankingPosition] thresholds.
 * - Duel and Team vs Team battles rely on [isDraw].
 *
 * Take into account that battles such as [RankedBattle] might not be finished
 * at the time of querying, thus it will not fall under [isVictory], [isDefeat]
 * or [isDraw].
 *
 * ⚠️ This extension is experimental because in-game behavior may vary:
 * - Friendly matches are static.
 * - Trophy calculations may consider some losses as neutral.
 */
val Battle.isDrawOrNeutral: Boolean
    get() {
        return when (this) {
            is CooperativeBattle -> result.isDraw
            is TeamRankingBattle -> rank == RankingPosition.THIRD
            is SoloRankingBattle -> rank == RankingPosition.FIFTH
            is RankedBattle -> result?.isDraw == true
            is DuelBattle -> result.isDraw
            is TeamVsTeamBattle -> result?.isDraw == true
            is TrophyLeagueBattle -> return trophyChange.isUnchanged
        }
    }

/**
 * Counts the number of victories in this list of [Battle]s.
 *
 * Relies on [Battle.isVictory] to determine which battles are considered victories.
 *
 * ⚠️ This API is experimental because the underlying determination of a victory
 * may vary depending on battle type, context, or in-game mechanics.
 *
 * @return A [VictoryAmount] representing the total victories in the list.
 */
val List<Battle>.victoriesAmount: VictoryAmount
    get() = VictoryAmount(count { it.isVictory })

/**
 * Counts the number of defeats in this list of [Battle]s.
 *
 * Relies on [Battle.isDefeat] to determine which battles are considered defeats.
 *
 * ⚠️ This API is experimental because the underlying determination of a defeat
 * may vary depending on battle type, context, or in-game mechanics.
 *
 * @return A [DefeatAmount] representing the total defeats in the list.
 */
val List<Battle>.defeatsAmount: DefeatAmount
    get() = DefeatAmount(count { it.isDefeat })

/**
 * Counts the number of draws or neutral outcomes in this list of [Battle]s.
 *
 * Relies on [Battle.isDrawOrNeutral] to determine which battles are considered
 * draws or neutral. Includes cases where the player neither won nor lost.
 *
 * ⚠️ This API is experimental because the underlying determination may vary
 * depending on battle type, trophy amount, or other in-game mechanics.
 *
 * @return A [DrawAmount] representing the total draws or neutral battles in the list.
 */
val List<Battle>.drawsOrNeutralsAmount: DrawAmount
    get() = DrawAmount(count { it.isDrawOrNeutral })

package krawler.server.player.application.battle

import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents the player who earned the Battle Star in a Brawl Stars match.
 *
 * The Battle Star is typically awarded to the most valuable player (MVP) in certain match types.
 */
sealed interface BattleStarPlayer {
    /**
     * The unique tag of the player (e.g., `#ABC123`).
     */
    val tag: PlayerTag

    /**
     * The name of the player as shown in-game.
     */
    val name: PlayerName

    /**
     * The brawler the player used in this battle.
     */
    val brawler: BattleBrawler
}

/**
 * A Battle Star player in a Friendly battle context.
 */
data class FriendlyBattleStarPlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: FriendlyBattleBrawler,
) : BattleStarPlayer

/**
 * A Battle Star player in a Trophy League battle context.
 */
data class TrophyLeagueBattleStarPlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: TrophyLeagueBattleBrawler,
) : BattleStarPlayer

/**
 * A Battle Star player in a Ranked battle context (e.g., Power League or Club League).
 */
data class RankedBattleStarPlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: RankedBattleBrawler,
) : BattleStarPlayer

/**
 * Returns `true` if this [BattleStarPlayer] is from a Friendly battle.
 * Supports smart casting to [FriendlyBattleStarPlayer].
 */
@OptIn(ExperimentalContracts::class)
fun BattleStarPlayer.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is FriendlyBattleStarPlayer)
    }
    return this is FriendlyBattleStarPlayer
}

/**
 * Returns `true` if this [BattleStarPlayer] is from a Trophy League battle.
 * Supports smart casting to [TrophyLeagueBattleStarPlayer].
 */
@OptIn(ExperimentalContracts::class)
fun BattleStarPlayer.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueBattleStarPlayer)
    }
    return this is TrophyLeagueBattleStarPlayer
}

/**
 * Returns `true` if this [BattleStarPlayer] is from a Ranked battle (Power/Club League).
 * Supports smart casting to [RankedBattleStarPlayer].
 */
@OptIn(ExperimentalContracts::class)
fun BattleStarPlayer.isRanked(): Boolean {
    contract {
        returns(true) implies (this@isRanked is RankedBattleStarPlayer)
    }
    return this is RankedBattleStarPlayer
}

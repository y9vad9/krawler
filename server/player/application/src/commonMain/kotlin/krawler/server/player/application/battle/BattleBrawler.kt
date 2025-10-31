package krawler.server.player.application.battle

import krawler.server.player.application.PlayerRankedStage
import krawler.server.player.application.Trophies
import krawler.server.player.application.brawler.BrawlerId
import krawler.server.player.application.brawler.BrawlerName
import krawler.server.player.application.brawler.BrawlerPowerLevel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents a Brawler used in a battle context.
 *
 * This sealed interface abstracts the common properties of a brawler participating
 * in any type of battle: [id], [name], and [powerLevel].
 *
 * Subtypes provide additional data relevant to the specific battle mode:
 * - [TrophyLeagueBattleBrawler]: For standard multiplayer battles with trophies.
 * - [RankedBattleBrawler]: For competitive ranked modes.
 * - [FriendlyBattleBrawler]: For friendly matches without progression.
 */
sealed interface BattleBrawler {
    /** Unique identifier of the brawler. */
    val id: BrawlerId

    /** The localized or canonical name of the brawler. */
    val name: BrawlerName

    /** The brawler's power level at the time of the battle. */
    val powerLevel: BrawlerPowerLevel
}

/**
 * A brawler used in a Ranked match.
 *
 * Includes the player's current [RankedStage] at the time of the match.
 *
 * @property id The unique identifier of the brawler.
 * @property name The display name of the brawler.
 * @property powerLevel The power level of the brawler during the match.
 * @property rankedStage The ranked progression stage for this brawler at the time of the match.
 */
data class RankedBattleBrawler(
    override val id: BrawlerId,
    override val name: BrawlerName,
    override val powerLevel: BrawlerPowerLevel,
    val rankedStage: PlayerRankedStage,
) : BattleBrawler

/**
 * A brawler used in a Trophy League match.
 *
 * Includes trophy count at the time of the match, in addition to basic battle properties.
 *
 * @property id The unique identifier of the brawler.
 * @property name The name of the brawler.
 * @property powerLevel The power level of the brawler.
 * @property trophies The number of trophies the brawler had before the match.
 */
data class TrophyLeagueBattleBrawler(
    override val id: BrawlerId,
    override val name: BrawlerName,
    override val powerLevel: BrawlerPowerLevel,
    val trophies: Trophies,
) : BattleBrawler


/**
 * A brawler used in a Friendly battle.
 *
 * In friendly matches, power levels do not affect performance.
 * This type always returns [BrawlerPowerLevel.MAX] regardless of input.
 *
 * @property id The unique identifier of the brawler.
 * @property name The display name of the brawler.
 */
data class FriendlyBattleBrawler(
    override val id: BrawlerId,
    override val name: BrawlerName,
) : BattleBrawler {

    /**
     * The power level of the brawler. For friendly battles,
     * it's always the maximum available level.
     */
    override val powerLevel: BrawlerPowerLevel
        get() = BrawlerPowerLevel.MAX
}

/**
 * Returns `true` if this [BattleBrawler] is a [TrophyLeagueBattleBrawler].
 *
 * Enables smart casting to [TrophyLeagueBattleBrawler] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleBrawler.isInTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isInTrophyLeague is TrophyLeagueBattleBrawler)
    }
    return this is TrophyLeagueBattleBrawler
}

/**
 * Returns `true` if this [BattleBrawler] is a [RankedBattleBrawler].
 *
 * Enables smart casting to [RankedBattleBrawler] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleBrawler.isInRanked(): Boolean {
    contract {
        returns(true) implies (this@isInRanked is RankedBattleBrawler)
    }
    return this is RankedBattleBrawler
}

/**
 * Returns `true` if this [BattleBrawler] is a [FriendlyBattleBrawler].
 *
 * Enables smart casting to [FriendlyBattleBrawler] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleBrawler.isInFriendly(): Boolean {
    contract {
        returns(true) implies (this@isInFriendly is FriendlyBattleBrawler)
    }
    return this is FriendlyBattleBrawler
}

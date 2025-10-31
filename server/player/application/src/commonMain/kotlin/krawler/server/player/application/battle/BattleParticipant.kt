package krawler.server.player.application.battle

import krawler.server.player.application.PlayerName
import krawler.server.player.application.PlayerTag
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Represents a participant in a Brawl Stars battle.
 *
 * A participant can either be a real player or a bot. This interface abstracts over
 * both cases and provides access to shared information such as the player's tag,
 * name, and chosen brawler.
 */
sealed interface BattleParticipant {
    /** The unique player tag identifying this participant. */
    val tag: PlayerTag

    /** The display name of this participant. */
    val name: PlayerName

    /** The brawler selected by this participant for the battle. */
    val brawler: BattleBrawler
}

/**
 * A bot that participated in the battle.
 *
 * This represents an AI-controlled participant. Occurs only in friendly battles.
 */
data class BattleBot(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: FriendlyBattleBrawler,
) : BattleParticipant

/**
 * Represents a player participating in a Brawl Stars battle.
 *
 * This sealed interface serves as a common abstraction for all types of players involved
 * in different battle modes, such as friendly, ranked, and trophy league battles.
 *
 * All implementations expose basic player identity and selected brawler information.
 *
 * @see FriendlyBattlePlayer
 * @see RankedBattlePlayer
 * @see TrophyLeagueBattlePlayer
 */
sealed interface BattlePlayer : BattleParticipant

/**
 * Represents a player participating in a
 **[friendly](https://brawlstars.fandom.com/wiki/Friendly_Battles)** battle.
 *
 * Includes player identity and the [brawler] they used in the friendly match.
 *
 * @property tag Unique tag identifying the player.
 * @property name In-game name of the player.
 * @property brawler Brawler used by the player during the friendly battle.
 */
data class FriendlyBattlePlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: FriendlyBattleBrawler,
) : BattlePlayer

/**
 * Represents a player participating in a
 **[ranked](https://brawlstars.fandom.com/wiki/Ranked)** battle.
 *
 * Includes player identity and the [brawler] they used in the ranked match.
 *
 * @property tag Unique tag identifying the player.
 * @property name In-game name of the player.
 * @property brawler Brawler used by the player during the ranked battle.
 */
data class RankedBattlePlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: RankedBattleBrawler,
) : BattlePlayer

/**
 * Represents a player participating in a
 **[trophy league](https://brawlstars.fandom.com/wiki/Trophies)** battle.
 *
 * Includes player identity and the [brawler] they used in the trophy league match.
 *
 * @property tag Unique tag identifying the player.
 * @property name In-game name of the player.
 * @property brawler Brawler used by the player during the trophy league battle.
 */
data class TrophyLeagueBattlePlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawler: TrophyLeagueBattleBrawler,
) : BattlePlayer

/**
 * Returns `true` if this [BattlePlayer] is a [FriendlyBattlePlayer].
 *
 * Use this to safely smart-cast the instance to [FriendlyBattlePlayer] when true.
 */
@OptIn(ExperimentalContracts::class)
fun BattlePlayer.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is FriendlyBattlePlayer)
    }
    return this is FriendlyBattlePlayer
}

/**
 * Returns `true` if this [BattlePlayer] is a [RankedBattlePlayer].
 *
 * Use this to safely smart-cast the instance to [RankedBattlePlayer] when true.
 */
@OptIn(ExperimentalContracts::class)
fun BattlePlayer.isRanked(): Boolean {
    contract {
        returns(true) implies (this@isRanked is RankedBattlePlayer)
    }
    return this is RankedBattlePlayer
}

/**
 * Returns `true` if this [BattlePlayer] is a [TrophyLeagueBattlePlayer].
 *
 * Use this to safely smart-cast the instance to [TrophyLeagueBattlePlayer] when true.
 */
@OptIn(ExperimentalContracts::class)
fun BattlePlayer.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueBattlePlayer)
    }
    return this is TrophyLeagueBattlePlayer
}

/**
 * Returns `true` if this [BattleParticipant] is a [BattlePlayer].
 *
 * Enables smart casting to [BattlePlayer] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleParticipant.isPlayer(): Boolean {
    contract {
        returns(true) implies (this@isPlayer is BattlePlayer)
    }
    return this is BattlePlayer
}

/**
 * Returns `true` if this [BattleParticipant] is a [BattleBot].
 *
 * Enables smart casting to [BattleBot] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleParticipant.isBot(): Boolean {
    contract {
        returns(true) implies (this@isBot is BattleBot)
    }
    return this is BattleBot
}

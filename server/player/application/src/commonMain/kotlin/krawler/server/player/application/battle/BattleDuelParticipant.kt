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
sealed interface BattleDuelParticipant {
    /** The unique player tag identifying this participant. */
    val tag: PlayerTag

    /** The display name of this participant. */
    val name: PlayerName

    /** The brawler selected by this participant for the battle. */
    val brawlers: List<BattleBrawler>
}

/**
 * A bot that participated in the duel battle.
 *
 * This represents an AI-controlled participant. It occurs only in friendly battles.
 */
data class BattleDuelBot(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawlers: List<FriendlyBattleBrawler>,
) : BattleDuelParticipant

/**
 * Represents a player participating in a Brawl Stars battle.
 *
 * This sealed interface serves as a common abstraction for all types of players involved
 * in different battle modes, such as friendly, ranked, and trophy league battles.
 *
 * All implementations expose basic player identity and selected brawler information.
 *
 * @see FriendlyBattleDuelPlayer
 * @see TrophyLeagueBattleDuelPlayer
 */
sealed interface BattleDuelPlayer : BattleDuelParticipant

/**
 * Represents a player participating in a
 **[friendly](https://brawlstars.fandom.com/wiki/Friendly_Battles)** battle.
 *
 * Includes player identity and the [brawler] they used in the friendly match.
 *
 * @property tag Unique tag identifying the player.
 * @property name In-game name of the player.
 * @property brawlers Brawlers used by the player during the friendly duel battle.
 */
data class FriendlyBattleDuelPlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawlers: List<FriendlyBattleBrawler>,
) : BattleDuelPlayer

/**
 * Represents a player participating in a
 * [trophy league](https://brawlstars.fandom.com/wiki/Trophies) battle.
 *
 * Includes player identity and the [brawlers] they used in the trophy league match.
 *
 * @property tag Unique tag identifying the player.
 * @property name In-game name of the player.
 * @property brawlers Brawlers used by the player during the trophy league duel battle.
 */
data class TrophyLeagueBattleDuelPlayer(
    override val tag: PlayerTag,
    override val name: PlayerName,
    override val brawlers: List<TrophyLeagueBattleBrawler>,
) : BattleDuelPlayer

/**
 * Returns `true` if this [BattleDuelPlayer] is a [FriendlyBattleDuelPlayer].
 *
 * Use this to safely smart-cast the instance to [FriendlyBattleDuelPlayer] when true.
 */
@OptIn(ExperimentalContracts::class)
fun BattleDuelPlayer.isFriendly(): Boolean {
    contract {
        returns(true) implies (this@isFriendly is FriendlyBattleDuelPlayer)
    }
    return this is FriendlyBattleDuelPlayer
}

/**
 * Returns `true` if this [BattleDuelPlayer] is a [TrophyLeagueBattleDuelPlayer].
 *
 * Use this to safely smart-cast the instance to [TrophyLeagueBattleDuelPlayer] when true.
 */
@OptIn(ExperimentalContracts::class)
fun BattleDuelPlayer.isTrophyLeague(): Boolean {
    contract {
        returns(true) implies (this@isTrophyLeague is TrophyLeagueBattleDuelPlayer)
    }
    return this is TrophyLeagueBattleDuelPlayer
}

/**
 * Returns `true` if this [BattleDuelParticipant] is a [BattleDuelPlayer].
 *
 * Enables smart casting to [BattlePlayer] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleDuelParticipant.isPlayer(): Boolean {
    contract {
        returns(true) implies (this@isPlayer is BattleDuelPlayer)
    }
    return this is BattleDuelPlayer
}

/**
 * Returns `true` if this [BattleParticipant]
 * is a [BattleDuelBot].
 *
 * Enables smart casting to [BattleBot] within the calling scope.
 */
@OptIn(ExperimentalContracts::class)
fun BattleDuelParticipant.isBot(): Boolean {
    contract {
        returns(true) implies (this@isBot is BattleDuelBot)
    }
    return this is BattleDuelBot
}

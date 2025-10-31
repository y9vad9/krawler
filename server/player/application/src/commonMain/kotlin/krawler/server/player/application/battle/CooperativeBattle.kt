package krawler.server.player.application.battle

import kotlin.time.Instant
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Marker interface for all **cooperative** battles in Brawl Stars.
 *
 * Cooperative battles are team-based game modes where players work together
 * to achieve a common objective, rather than competing directly against each other.
 *
 * Examples include game modes like Last Stand and Robo Rumble.
 */
sealed interface CooperativeBattle : TeamBattle {
    /**
     * Represents the result of the cooperative battle.
     */
    val result: BattleResult
    /**
     * The participants in this cooperative battle.
     */
    val players: BattleParticipants
}

/**
 * Represents a **friendly** cooperative battle — an unranked cooperative match
 * played casually or for practice.
 */
sealed interface FriendlyCooperativeBattle : CooperativeBattle, FriendlyBattle {
    /**
     * The participants in this friendly cooperative battle. Can be either
     * bot or real player.
     */
    override val players: BattleParticipants
}

/**
 * Represents a non-friendly cooperative battle — a cooperative match that is not
 * considered friendly or casual.
 */
sealed interface EventCooperativeBattle : CooperativeBattle {
    /**
     * The participants in this cooperative battle.
     */
    override val players: TrophyLeagueBattlePlayers

    /**
     * Represents official event returned from Brawl Stars API. For competitive
     * battles, it's always an official event.
     */
    override val event: OfficialBattleEvent
}

/**
 * Represents a Last Stand battle, a cooperative game mode where players team up
 * to defend against waves of enemies.
 *
 * This interface extends [CooperativeBattle] and adds a specific level property
 * indicating the difficulty or stage of the Last Stand battle.
 *
 * Last Stand battles can be either friendly or competitive depending on the
 * implementing subtype.
 */
sealed interface LastStandBattle : CooperativeBattle {
    /**
     * The [EnemyBotsLevel] representing the difficulty or progression
     * stage of the battle.
     */
    val level: EnemyBotsLevel
}

/**
 * A **friendly** Last Stand battle — an unranked cooperative match where players
 * team up to defend against waves of enemies in the Last Stand game mode.
 *
 * > **Note:** These battles are intended for forward-compatibility and currently do not exist
 * > in the game.
 *
 * These battles are typically casual or practice matches and include:
 * - The difficulty level ([level]) of the Last Stand encounter.
 * - The participants ([players]), which can include bots or real players.
 * - The timestamp when the battle occurred ([time]).
 * - The event metadata ([event]) describing the map and mode.
 *
 * @property level The difficulty or progression stage of the Last Stand battle.
 * @property players The participants involved in the friendly cooperative battle.
 * @property time The time when the battle was played.
 * @property event The event (map and mode) in which this battle took place.
 * @property result The result of the battle (VICTORY, DEFEAT, DRAW).
 */
data class FriendlyLastStandBattle(
    override val level: EnemyBotsLevel,
    override val players: BattleParticipants,
    override val time: Instant,
    override val event: BattleEvent,
    override val result: BattleResult,
) : LastStandBattle, FriendlyCooperativeBattle

/**
 * A **competitive** Last Stand battle — a ranked cooperative match where players
 * team up to defend against waves of enemies with stakes and ranking implications.
 *
 * These battles include:
 * - The difficulty level ([level]) of the Last Stand encounter.
 * - The participants ([players]) with trophy league specific stats.
 * - The timestamp when the battle occurred ([time]).
 * - The event metadata ([event]) describing the map and mode.
 *
 * @property level The difficulty or progression stage of the Last Stand battle.
 * @property players The ranked participants involved in the competitive cooperative battle.
 * @property time The time when the battle was played.
 * @property event The event (map and mode) in which this battle took place.
 * @property result The result of the battle.
 */
data class EventLastStandBattle(
    override val level: EnemyBotsLevel,
    override val players: TrophyLeagueBattlePlayers,
    override val time: Instant,
    override val event: OfficialBattleEvent,
    override val result: BattleResult,
) : LastStandBattle, EventCooperativeBattle

/**
 * Represents a classic cooperative battle in Brawl Stars.
 *
 * Classic cooperative battles are team-based game modes where players cooperate without
 * additional unique properties like Last Stand’s level. This interface is used as a
 * general marker for such cooperative battles.
 *
 * > **Note**: Some battles currently classified as classic cooperative may
 * > be moved in the future to more specific interfaces if additional unique
 * > attributes are identified that cannot be expressed through this general type.
 */
sealed interface ClassicCooperativeBattle : CooperativeBattle {
    /**
     * The participants in this classic cooperative battle.
     */
    override val players: BattleParticipants
}

/**
 * Represents a **friendly** classic cooperative battle — an unranked cooperative
 * match played casually or for practice.
 *
 * > **Note**: Some battles currently classified as classic cooperative may
 * > be moved in the future to more specific interfaces if additional unique
 * > attributes are identified that cannot be expressed through this general type.
 */
data class FriendlyClassicCooperativeBattle(
    override val players: BattleParticipants,
    override val time: Instant,
    override val event: BattleEvent,
    override val result: BattleResult,
) : ClassicCooperativeBattle, FriendlyCooperativeBattle

/**
 * Represents a **competitive** classic cooperative battle — a cooperative match that
 * is not considered friendly or casual.
 *
 * > **Note**: Some battles currently classified as classic cooperative may
 * > be moved in the future to more specific interfaces if additional unique
 * > attributes are identified that cannot be expressed through this general type.
 */
data class EventClassicCooperativeBattle(
    override val players: TrophyLeagueBattlePlayers,
    override val time: Instant,
    override val event: OfficialBattleEvent,
    override val result: BattleResult,
) : ClassicCooperativeBattle, EventCooperativeBattle

/**
 * Returns `true` if this cooperative battle is a [FriendlyCooperativeBattle].
 *
 * This check is contract-aware: within `if (isFriendlyCooperative())` blocks,
 * `this` is smart-cast to [FriendlyCooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun CooperativeBattle.isFriendlyCooperative(): Boolean {
    contract {
        returns(true) implies (this@isFriendlyCooperative is FriendlyCooperativeBattle)
    }
    return this is FriendlyCooperativeBattle
}

/**
 * Returns `true` if this cooperative battle is an [EventCooperativeBattle].
 *
 * This check is contract-aware: within `if (isEventCooperative())` blocks,
 * `this` is smart-cast to [EventCooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun CooperativeBattle.isEventCooperative(): Boolean {
    contract {
        returns(true) implies (this@isEventCooperative is EventCooperativeBattle)
    }
    return this is EventCooperativeBattle
}

/**
 * Returns `true` if this cooperative battle is a [LastStandBattle].
 *
 * This check is contract-aware: within `if (isLastStandBattle())` blocks,
 * `this` is smart-cast to [LastStandBattle].
 */
@OptIn(ExperimentalContracts::class)
fun CooperativeBattle.isLastStandBattle(): Boolean {
    contract {
        returns(true) implies (this@isLastStandBattle is LastStandBattle)
    }
    return this is LastStandBattle
}

/**
 * Returns `true` if this cooperative battle is a [ClassicCooperativeBattle].
 *
 * This check is contract-aware: within `if (isClassicCooperativeBattle())` blocks,
 * `this` is smart-cast to [ClassicCooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun CooperativeBattle.isClassicCooperativeBattle(): Boolean {
    contract {
        returns(true) implies (this@isClassicCooperativeBattle is ClassicCooperativeBattle)
    }
    return this is ClassicCooperativeBattle
}

/**
 * Returns `true` if this cooperative battle is a friendly Last Stand battle.
 *
 * This check is contract-aware: within `if (isFriendlyLastStandBattle())` blocks,
 * `this` is smart-cast to [FriendlyLastStandBattle].
 */
@OptIn(ExperimentalContracts::class)
fun LastStandBattle.isFriendlyLastStandBattle(): Boolean {
    contract {
        returns(true) implies (this@isFriendlyLastStandBattle is FriendlyLastStandBattle)
    }
    return this is FriendlyLastStandBattle
}

/**
 * Returns `true` if this cooperative battle is a competitive Last Stand battle.
 *
 * This check is contract-aware: within `if (isEventLastStandBattle())` blocks,
 * `this` is smart-cast to [EventLastStandBattle].
 */
@OptIn(ExperimentalContracts::class)
fun LastStandBattle.isEventLastStandBattle(): Boolean {
    contract {
        returns(true) implies (this@isEventLastStandBattle is EventLastStandBattle)
    }
    return this is EventLastStandBattle
}

/**
 * Returns `true` if this cooperative battle is a friendly classic cooperative battle.
 *
 * This check is contract-aware: within `if (isFriendlyClassicCooperativeBattle())` blocks,
 * `this` is smart-cast to [FriendlyClassicCooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun ClassicCooperativeBattle.isFriendlyClassicCooperativeBattle(): Boolean {
    contract {
        returns(true) implies (this@isFriendlyClassicCooperativeBattle is FriendlyClassicCooperativeBattle)
    }
    return this is FriendlyClassicCooperativeBattle
}

/**
 * Returns `true` if this cooperative battle is a competitive classic cooperative battle.
 *
 * This check is contract-aware: within `if (isEventClassicCooperativeBattle())` blocks,
 * `this` is smart-cast to [EventClassicCooperativeBattle].
 */
@OptIn(ExperimentalContracts::class)
fun ClassicCooperativeBattle.isEventClassicCooperativeBattle(): Boolean {
    contract {
        returns(true) implies (this@isEventClassicCooperativeBattle is EventClassicCooperativeBattle)
    }
    return this is EventClassicCooperativeBattle
}

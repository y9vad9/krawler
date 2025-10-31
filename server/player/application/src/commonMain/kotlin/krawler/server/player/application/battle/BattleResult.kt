package krawler.server.player.application.battle

/**
 * Represents the result of a team-based battle (or in duels) in Brawl Stars.
 *
 * This enum distinguishes between the three possible outcomes a team can experience:
 * - [VICTORY]: The team won the match.
 * - [DRAW]: The match ended with no winner or loser.
 * - [DEFEAT]: The team lost the match.
 */
enum class BattleResult {
    /** The team successfully won the battle. */
    VICTORY,

    /** The battle ended without a winner or loser. */
    DRAW,

    /** The team lost the battle. */
    DEFEAT,
}

/**
 * Returns `true` if this [BattleResult] represents a victory.
 */
val BattleResult.isVictory: Boolean
    get() = this == BattleResult.VICTORY

/**
 * Returns `true` if this [BattleResult] represents a draw.
 */
val BattleResult.isDraw: Boolean
    get() = this == BattleResult.DRAW

/**
 * Returns `true` if this [BattleResult] represents a defeat.
 */
val BattleResult.isDefeat: Boolean
    get() = this == BattleResult.DEFEAT

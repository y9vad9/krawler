package krawler.server.player.application.battle

import kotlin.jvm.JvmInline

/**
 * Represents the display name of a **Last Stand** difficulty level in Brawl Stars.
 *
 * This is the human-readable label shown in-game for each difficulty, for example:
 * - `"Hard"`
 * - `"Expert"`
 * - `"Master"`
 * - `"Insane"`
 * - `"Insane II"`
 * - `"Insane III"`
 * - `"Insane IV"`
 *
 * This type is a simple wrapper around a [String] to provide type safety
 * and avoid mixing raw strings with other identifiers in the codebase.
 *
 * @property rawString The raw string value of the difficulty's display name, exactly as it appears in the game data.
 */
@JvmInline
value class EnemyBotsLevelName(
    val rawString: String,
)

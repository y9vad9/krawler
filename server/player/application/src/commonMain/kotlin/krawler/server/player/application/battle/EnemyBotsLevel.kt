package krawler.server.player.application.battle

/**
 * Represents a specific difficulty level for the **Last Stand** game mode in Brawl Stars.
 *
 * A [EnemyBotsLevel] is composed of:
 * - A numeric [id] ([EnemyBotsLevelId]) — used internally to determine difficulty ordering.
 * - A human-readable [name] ([EnemyBotsLevelName]) — displayed in-game and in API responses.
 *
 * Difficulty levels progress in increasing order of challenge:
 * 1. Hard
 * 2. Expert
 * 3. Master
 * 4. Insane
 * 5. Insane II
 * 6. Insane III
 * 7. Insane IV
 *
 * This type pairs both the numeric and textual representations to allow for
 * consistent and type-safe handling of Last Stand levels in domain logic.
 *
 * @property id   The unique numeric identifier for this Last Stand difficulty level.
 * @property name The display name for this difficulty level, as shown in-game.
 */
data class EnemyBotsLevel(
    val id: EnemyBotsLevelId,
    val name: EnemyBotsLevelName,
)

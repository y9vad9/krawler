package krawler.server.player.application.battle

/**
 * Represents a **Last Stand** difficulty level in Brawl Stars.
 *
 * The [Last Stand](https://brawlstars.fandom.com/wiki/Last_Stand) game mode
 * is a special unranked event where three players must protect an AI-controlled 8-Bit
 * from waves of enemy robots. Difficulty increases progressively with each win.
 *
 * Difficulty levels are represented as integer values:
 * - HARD (1)
 * - EXPERT (2)
 * - MASTER (3)
 * - INSANE (4)
 * - INSANE 2 (5)
 * - INSANE 3 (6)
 * - INSANE 4 (7)
 *
 * This type is comparable, allowing easy ranking and threshold checks.
 *
 * @property rawInt The raw integer identifier for the difficulty level.
 */
@JvmInline
value class EnemyBotsLevelId(val rawInt: Int) : Comparable<EnemyBotsLevelId> {

    init {
        require(rawInt in MIN_VALUE..MAX_VALUE) {
            "Invalid EnemyBotsLevelId value: $rawInt. Valid range is $MIN_VALUE..$MAX_VALUE."
        }
    }

    override fun compareTo(other: EnemyBotsLevelId): Int = rawInt.compareTo(other.rawInt)

    companion object {
        /** Minimum valid Last Stand difficulty level. */
        const val MIN_VALUE: Int = 1

        /** Maximum valid Last Stand difficulty level. */
        const val MAX_VALUE: Int = 7

        /** Hard difficulty (level 1). */
        val HARD: EnemyBotsLevelId = EnemyBotsLevelId(1)

        /** Expert difficulty (level 2). */
        val EXPERT: EnemyBotsLevelId = EnemyBotsLevelId(2)

        /** Master difficulty (level 3). */
        val MASTER: EnemyBotsLevelId = EnemyBotsLevelId(3)

        /** Insane difficulty (level 4). */
        val INSANE: EnemyBotsLevelId = EnemyBotsLevelId(4)

        /** Insane 2 difficulty (level 5). */
        val INSANE_2: EnemyBotsLevelId = EnemyBotsLevelId(5)

        /** Insane 3 difficulty (level 6). */
        val INSANE_3: EnemyBotsLevelId = EnemyBotsLevelId(6)

        /** Insane 4 difficulty (level 7). */
        val INSANE_4: EnemyBotsLevelId = EnemyBotsLevelId(7)
    }
}

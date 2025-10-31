package krawler.server.player.application.battle

import kotlin.jvm.JvmInline

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
value class EnemyBotsLevelId private constructor(val rawInt: Int) : Comparable<EnemyBotsLevelId> {
    /**
     * Compares this level with another level by their [rawInt] values.
     *
     * @param other The other difficulty level to compare to.
     * @return A negative number if this is less than [other], zero if equal,
     *         or a positive number if greater.
     */
    override fun compareTo(other: EnemyBotsLevelId): Int = rawInt.compareTo(other.rawInt)

    companion object {
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

        /**
         * Checks whether the given [raw] value corresponds to a valid Last Stand difficulty level.
         *
         * @param raw The integer to check.
         * @return `true` if [raw] is greater than 0.
         */
        fun isValid(raw: Int): Boolean =
            raw > 0

        /**
         * Creates a [EnemyBotsLevelId] from the given [raw] value, wrapped in a [Result].
         *
         * @param raw The integer value to create from.
         * @return A [Result] containing the [EnemyBotsLevelId] if valid, otherwise a failure.
         */
        fun create(raw: Int): Result<EnemyBotsLevelId> =
            if (isValid(raw)) Result.success(EnemyBotsLevelId(raw))
            else Result.failure(IllegalArgumentException("Invalid LastStandLevelId value: $raw"))

        /**
         * Creates a [EnemyBotsLevelId] from the given [raw] value, or returns `null` if invalid.
         *
         * @param raw The integer value to create from.
         * @return The [EnemyBotsLevelId] if valid, otherwise `null`.
         */
        fun createOrNull(raw: Int): EnemyBotsLevelId? =
            if (isValid(raw)) EnemyBotsLevelId(raw) else null

        /**
         * Creates a [EnemyBotsLevelId] from the given [raw] value,
         * throwing an [IllegalArgumentException] if invalid.
         *
         * @param raw The integer value to create from.
         * @return The [EnemyBotsLevelId] if valid.
         * @throws IllegalArgumentException if [raw] is not within the valid range.
         */
        fun createOrThrow(raw: Int): EnemyBotsLevelId =
            create(raw).getOrThrow()
    }
}

// --- Specific level checks ---

/** Returns `true` if this is [EnemyBotsLevelId.HARD]. */
fun EnemyBotsLevelId.isHard(): Boolean = this == EnemyBotsLevelId.HARD

/** Returns `true` if this is [EnemyBotsLevelId.EXPERT]. */
fun EnemyBotsLevelId.isExpert(): Boolean = this == EnemyBotsLevelId.EXPERT

/** Returns `true` if this is [EnemyBotsLevelId.MASTER]. */
fun EnemyBotsLevelId.isMaster(): Boolean = this == EnemyBotsLevelId.MASTER

/** Returns `true` if this is [EnemyBotsLevelId.INSANE]. */
fun EnemyBotsLevelId.isInsane(): Boolean = this == EnemyBotsLevelId.INSANE

/** Returns `true` if this is [EnemyBotsLevelId.INSANE_2]. */
fun EnemyBotsLevelId.isInsane2(): Boolean = this == EnemyBotsLevelId.INSANE_2

/** Returns `true` if this is [EnemyBotsLevelId.INSANE_3]. */
fun EnemyBotsLevelId.isInsane3(): Boolean = this == EnemyBotsLevelId.INSANE_3

/** Returns `true` if this is [EnemyBotsLevelId.INSANE_4]. */
fun EnemyBotsLevelId.isInsane4(): Boolean = this == EnemyBotsLevelId.INSANE_4

// --- Tier / range checks ---

/**
 * Returns `true` if this level is at most Expert difficulty.
 *
 * @receiver The current Last Stand difficulty level.
 * @return `true` if this level is less than or equal to [EnemyBotsLevelId.EXPERT].
 */
fun EnemyBotsLevelId.isAtMostExpert(): Boolean =
    this <= EnemyBotsLevelId.EXPERT

/**
 * Returns `true` if this level is at least Master difficulty.
 *
 * @receiver The current Last Stand difficulty level.
 * @return `true` if this level is greater than or equal to [EnemyBotsLevelId.MASTER].
 */
fun EnemyBotsLevelId.isAtLeastMaster(): Boolean =
    this >= EnemyBotsLevelId.MASTER

/**
 * Returns `true` if this level is at least Insane difficulty.
 *
 * @receiver The current Last Stand difficulty level.
 * @return `true` if this level is greater than or equal to [EnemyBotsLevelId.INSANE].
 */
fun EnemyBotsLevelId.isAtLeastInsane(): Boolean =
    this >= EnemyBotsLevelId.INSANE

/**
 * Returns `true` if this level is in the Insane tier
 * (Insane, Insane 2, Insane 3, Insane 4).
 *
 * @receiver The current Last Stand difficulty level.
 */
fun EnemyBotsLevelId.isInsaneTier(): Boolean =
    this >= EnemyBotsLevelId.INSANE

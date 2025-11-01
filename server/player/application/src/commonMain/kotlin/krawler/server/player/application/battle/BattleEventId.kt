package krawler.server.player.application.battle

import krawler.server.player.application.battle.BattleEventId.Companion.MAX_VALUE
import krawler.server.player.application.battle.BattleEventId.Companion.MIN_VALUE

/**
 * Represents the unique identifier of a Brawl Stars event (e.g., for maps or game modes).
 *
 * This is a lightweight wrapper around an [Int], providing type safety and validation against known
 * valid event ID ranges.
 *
 * @property rawInt The raw integer ID as returned by the Brawl Stars API.
 */
@JvmInline
value class BattleEventId private constructor(val rawInt: Int) : Comparable<BattleEventId> {

    override fun compareTo(other: BattleEventId): Int = rawInt.compareTo(other.rawInt)

    companion object {
        /** Minimum valid event ID observed in the official Brawl Stars API. */
        const val MIN_VALUE: Int = 15_000_000

        /** Maximum valid event ID observed in the official Brawl Stars API. */
        const val MAX_VALUE: Int = 15_100_000

        /** Inclusive range of known valid event IDs. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /** Attempts to create a [BattleEventId], returning a type-safe result. */
        fun create(int: Int): FactoryResult =
            when {
                int < MIN_VALUE -> FactoryResult.TooLow
                int > MAX_VALUE -> FactoryResult.TooHigh
                else -> FactoryResult.Success(BattleEventId(int))
            }

        /** Creates a [BattleEventId] or throws [IllegalArgumentException] if invalid. */
        fun createOrThrow(int: Int): BattleEventId =
            when (val result = create(int)) {
                is FactoryResult.Success -> result.value
                FactoryResult.TooLow -> throw IllegalArgumentException("Event ID below range ($VALUE_RANGE): $int")
                FactoryResult.TooHigh -> throw IllegalArgumentException("Event ID above range ($VALUE_RANGE): $int")
            }

        /** Creates a [BattleEventId] or returns `null` if invalid. */
        fun createOrNull(int: Int): BattleEventId? =
            (create(int) as? FactoryResult.Success)?.value
    }

    /** Type-safe factory result for [BattleEventId] creation attempts. */
    sealed interface FactoryResult {
        /** Creation succeeded with a valid [BattleEventId]. */
        data class Success(val value: BattleEventId) : FactoryResult

        /** Input was below [MIN_VALUE]. */
        object TooLow : FactoryResult

        /** Input was above [MAX_VALUE]. */
        object TooHigh : FactoryResult
    }
}

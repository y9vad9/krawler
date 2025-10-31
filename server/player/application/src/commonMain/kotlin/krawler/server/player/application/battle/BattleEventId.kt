package krawler.server.player.application.battle

import krawler.server.player.application.battle.BattleEventId.Companion.VALUE_RANGE
import krawler.server.player.application.battle.BattleEventId.Companion.create
import krawler.server.player.application.battle.BattleEventId.Companion.createOrNull
import krawler.server.player.application.battle.BattleEventId.Companion.createOrThrow
import kotlin.jvm.JvmInline

/**
 * Represents the unique identifier of a Brawl Stars event (e.g., for maps or game modes).
 *
 * This is a lightweight wrapper around an [Int], providing type safety and validation against known
 * valid event ID ranges. It can be constructed using [create], [createOrThrow], or [createOrNull],
 * and compared using standard operators.
 *
 * @property rawInt The raw integer ID as returned by the Brawl Stars API.
 */
@JvmInline
value class BattleEventId private constructor(val rawInt: Int) : Comparable<BattleEventId> {
    override fun compareTo(other: BattleEventId): Int = rawInt.compareTo(other.rawInt)

    companion object {
        /**
         * Minimum valid event ID observed in the official Brawl Stars API.
         */
        const val MIN_VALUE: Int = 15_000_000

        /**
         * Maximum valid event ID observed in the official Brawl Stars API.
         */
        const val MAX_VALUE: Int = 15_100_000

        /**
         * The inclusive range of known valid event IDs.
         */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Attempts to construct an [BattleEventId] from the given integer.
         *
         * Returns a [Result.success] if the input is within the valid range.
         * Otherwise, returns a [Result.failure] with [IllegalArgumentException].
         *
         * This is the preferred way to construct an [BattleEventId] when working with untrusted input.
         *
         * @param int The raw integer to wrap.
         * @return [Result] containing either a valid [BattleEventId] or a failure.
         */
        fun create(int: Int): Result<BattleEventId> = if (isValid(int)) {
            Result.success(BattleEventId(int))
        } else {
            Result.failure(IllegalArgumentException("Event ID $int is out of range: $VALUE_RANGE"))
        }

        /**
         * Constructs an [BattleEventId] from the given integer, or throws if invalid.
         *
         * This function throws an [IllegalArgumentException] if the input is outside
         * the known [VALUE_RANGE]. Use this only when input validity is guaranteed.
         *
         * @throws IllegalArgumentException if [int] is not within [VALUE_RANGE].
         */
        fun createOrThrow(int: Int): BattleEventId = create(int).getOrThrow()

        /**
         * Attempts to construct an [BattleEventId] or returns `null` if the input is invalid.
         *
         * @param int The integer to attempt wrapping.
         * @return A valid [BattleEventId] or `null` if the value is out of range.
         */
        fun createOrNull(int: Int): BattleEventId? = create(int).getOrNull()

        /**
         * Checks whether the provided integer is a valid [BattleEventId].
         *
         * @param int The integer to validate.
         * @return `true` if the integer is within [VALUE_RANGE], otherwise `false`.
         */
        fun isValid(int: Int): Boolean = int in VALUE_RANGE
    }
}

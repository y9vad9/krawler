package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerId.Companion.MIN_VALUE
import krawler.server.player.application.brawler.BrawlerId.Companion.create
import krawler.server.player.application.brawler.BrawlerId.Companion.createOrNull
import krawler.server.player.application.brawler.BrawlerId.Companion.createOrThrow

/**
 * Represents a unique identifier for a Brawler in Brawl Stars.
 *
 * This class wraps an integer ID that identifies a specific brawler.
 * Valid IDs fall within a predefined range ([MIN_VALUE] to [MAX_VALUE]).
 * Sorting by [BrawlerId] compares the underlying integer values.
 *
 * Use [create], [createOrNull], or [createOrThrow] to safely construct a [BrawlerId] instance.
 */
@JvmInline
value class BrawlerId private constructor(
    /** The underlying integer value of the brawler ID. */
    val rawInt: Int,
) : Comparable<BrawlerId> {

    companion object {
        /** Minimum valid value for a brawler ID. */
        const val MIN_VALUE: Int = 16_000_000

        /** Maximum valid value for a brawler ID. */
        const val MAX_VALUE: Int = 16_000_500

        /** Valid range of brawler ID values. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Attempts to create a [BrawlerId] from the given [input].
         *
         * Returns a [FactoryResult.Success] if valid, or one of the [FactoryResult.Failure] types if invalid.
         */
        fun create(input: Int): FactoryResult =
            when {
                input < MIN_VALUE -> FactoryResult.TooLow
                input > MAX_VALUE -> FactoryResult.TooHigh
                else -> FactoryResult.Success(BrawlerId(input))
            }

        /** Creates or throws [IllegalArgumentException] if invalid. */
        fun createOrThrow(input: Int): BrawlerId =
            when (val result = create(input)) {
                is FactoryResult.Success -> result.value
                FactoryResult.TooLow -> throw IllegalArgumentException("Brawler ID below range ($VALUE_RANGE): $input")
                FactoryResult.TooHigh -> throw IllegalArgumentException("Brawler ID above range ($VALUE_RANGE): $input")
            }

        /** Creates or returns `null` if invalid. */
        fun createOrNull(input: Int): BrawlerId? =
            (create(input) as? FactoryResult.Success)?.value

        /** Constant for Brawler ID representing Shelly. */
        val SHELLY: BrawlerId = BrawlerId(16_000_000)

        /** Constant for Brawler ID representing Colt. */
        val COLT: BrawlerId = BrawlerId(16_000_001)
    }

    override fun compareTo(other: BrawlerId): Int = rawInt.compareTo(other.rawInt)

    /**
     * Represents the result of attempting to create a [BrawlerId].
     *
     * This type-safe result makes validation outcomes explicit.
     */
    sealed interface FactoryResult {
        /** Creation succeeded with a valid [BrawlerId]. */
        data class Success(val value: BrawlerId) : FactoryResult

        /** Input below [MIN_VALUE]. */
        data object TooLow : FactoryResult

        /** Input above [MAX_VALUE]. */
        data object TooHigh : FactoryResult
    }
}

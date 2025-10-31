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

    /** Constants with constraints and validation */
    companion object {
        /** Minimum valid value for a brawler ID. */
        const val MIN_VALUE: Int = 16_000_000

        /** Maximum valid value for a brawler ID. */
        const val MAX_VALUE: Int = 16_000_500

        /** Valid range of brawler ID values. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Returns `true` if the given [input] is within the valid brawler ID range.
         *
         * This function performs a sanity check to ensure [input] falls within
         * the expected ID range for brawlers.
         */
        fun isValid(input: Int): Boolean =
            input in VALUE_RANGE

        /**
         * Attempts to create a [BrawlerId] from the given [input].
         *
         * Returns a [Result.success] containing a valid [BrawlerId], or
         * a [Result.failure] with an [IllegalArgumentException] if [input] is invalid.
         */
        fun create(input: Int): Result<BrawlerId> =
            if (isValid(input)) Result.success(BrawlerId(input))
            else Result.failure(IllegalArgumentException("Invalid brawler ID: $input"))

        /**
         * Creates a [BrawlerId] from [input] or throws [IllegalArgumentException] if invalid.
         */
        fun createOrThrow(input: Int): BrawlerId =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlerId] from [input], or returns `null` if invalid.
         */
        fun createOrNull(input: Int): BrawlerId? =
            create(input).getOrNull()

        /**
         * Constant for Brawler ID representing Shelly.
         * **[Learn more about Brawler on Brawlify](https://brawlify.com/brawlers/detail/Shelly)**
         */
        val SHELLY: BrawlerId = BrawlerId(16_000_000)

        /**
         * Constant for Brawler ID representing Colt.
         * **[Learn more about Brawler on Brawlify](https://brawlify.com/brawlers/detail/Colt)**
         */
        val COLT: BrawlerId = BrawlerId(16_000_001)
    }

    /**
     * Compares this [BrawlerId] with another based on their integer values.
     *
     * @param other The other [BrawlerId] to compare against.
     * @return A negative integer, zero, or a positive integer as this ID
     * is less than, equal to, or greater than the other ID.
     */
    override fun compareTo(other: BrawlerId): Int = rawInt.compareTo(other.rawInt)
}

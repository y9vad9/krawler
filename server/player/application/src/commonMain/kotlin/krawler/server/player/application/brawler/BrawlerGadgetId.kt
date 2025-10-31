package krawler.server.player.application.brawler

import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.MAX_VALUE
import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.MIN_VALUE
import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.create
import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.createOrNull
import krawler.server.player.application.brawler.BrawlerGadgetId.Companion.createOrThrow

/**
 * Represents a unique identifier for a Gadget in Brawl Stars.
 *
 * This class wraps an integer ID that identifies a specific gadget.
 * Valid IDs fall within a predefined range ([MIN_VALUE] to [MAX_VALUE]).
 * Sorting by [BrawlerGadgetId] compares the underlying integer values.
 *
 * Use [create], [createOrNull], or [createOrThrow] to safely construct a [BrawlerGadgetId] instance.
 */
@JvmInline
value class BrawlerGadgetId private constructor(
    /** The underlying integer value of the gadget ID. */
    val rawInt: Int,
) : Comparable<BrawlerGadgetId> {
    /** Constants with constraints and validation */
    companion object {
        /** Minimum valid value for a gadget ID. */
        const val MIN_VALUE: Int = 23_000_000

        /** Maximum valid value for a gadget ID. */
        const val MAX_VALUE: Int = 23_010_000

        /** Valid range of gadget ID values. */
        val VALUE_RANGE: IntRange = MIN_VALUE..MAX_VALUE

        /**
         * Returns `true` if the given [input] is within the valid gadget ID range.
         *
         * This function performs a sanity check to ensure [input] falls within
         * the expected ID range for gadgets.
         */
        fun isValid(input: Int): Boolean =
            input in VALUE_RANGE

        /**
         * Attempts to create a [BrawlerGadgetId] from the given [input].
         *
         * Returns a [Result.success] containing a valid [BrawlerGadgetId], or
         * a [Result.failure] with an [IllegalArgumentException] if [input] is invalid.
         */
        fun create(input: Int): Result<BrawlerGadgetId> =
            if (isValid(input)) Result.success(BrawlerGadgetId(input))
            else Result.failure(IllegalArgumentException("Invalid gadget ID: $input"))

        /**
         * Creates a [BrawlerGadgetId] from [input] or throws [IllegalArgumentException] if invalid.
         */
        fun createOrThrow(input: Int): BrawlerGadgetId =
            create(input).getOrThrow()

        /**
         * Creates a [BrawlerGadgetId] from [input], or returns `null` if invalid.
         */
        fun createOrNull(input: Int): BrawlerGadgetId? =
            create(input).getOrNull()
    }

    /**
     * Compares this [BrawlerGadgetId] with another based on their integer values.
     *
     * @param other The other [BrawlerGadgetId] to compare against.
     * @return A negative integer, zero, or a positive integer as this ID
     * is less than, equal to, or greater than the other ID.
     */
    override fun compareTo(other: BrawlerGadgetId): Int = rawInt.compareTo(other.rawInt)
}

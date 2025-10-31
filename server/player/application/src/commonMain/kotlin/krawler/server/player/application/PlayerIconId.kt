package krawler.server.player.application

import krawler.server.player.application.PlayerIconId.Companion.MIN_VALUE

/**
 * Represents a unique identifier for a player icon in Brawl Stars.
 *
 * Player icon IDs are integer values used to reference avatar icons in the Brawl Stars API.
 * These IDs typically start at **28000000** and increase from there, corresponding to visual
 * assets in the game client.
 *
 * This class is comparable based on its [raw] integer value.
 *
 * @property raw The raw integer ID of the player icon.
 */
@JvmInline
value class PlayerIconId private constructor(val raw: Int) : Comparable<PlayerIconId> {
    /** Constants with constraints and validation */
    companion object {
        /** Minimum allowed ID for a valid player icon. */
        const val MIN_VALUE: Int = 28_000_000

        /**
         * Checks whether the given [input] is a valid player icon ID.
         *
         * @return `true` if the ID is greater than or equal to [MIN_VALUE], otherwise `false`.
         */
        fun isValid(input: Int): Boolean = input >= MIN_VALUE

        /**
         * Attempts to create a [PlayerIconId] from the given [input].
         *
         * @return [Result.success] with a valid [PlayerIconId], or [Result.failure] with an [IllegalArgumentException].
         */
        fun create(input: Int): Result<PlayerIconId> {
            return if (isValid(input)) Result.success(PlayerIconId(input))
            else Result.failure(IllegalArgumentException("PlayerIconId must be >= $MIN_VALUE, but was $input"))
        }

        /**
         * Attempts to create a [PlayerIconId] from the given [input] or throws
         * n [IllegalArgumentException] if invalid.
         *
         * @throws IllegalArgumentException if the input is below [MIN_VALUE].
         */
        fun createOrThrow(input: Int): PlayerIconId = create(input).getOrThrow()

        /**
         * Attempts to create a [PlayerIconId] from the given [input] or returns `null` if invalid.
         */
        fun createOrNull(input: Int): PlayerIconId? = create(input).getOrNull()
    }

    /**
     * Compares this [PlayerIconId] with another by their [raw] values.
     */
    override fun compareTo(other: PlayerIconId): Int = raw.compareTo(other.raw)
}

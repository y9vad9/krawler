package krawler.server.player.application

import krawler.server.player.application.PlayerTrophies.Companion.create
import krawler.server.player.application.PlayerTrophies.Companion.createOrNull
import krawler.server.player.application.PlayerTrophies.Companion.createOrThrow

/**
 * Represents a player's current and highest trophy counts.
 *
 * The current count reflects the player's live progress. The highest count
 * is the peak ever reached. The current count must not exceed the highest.
 *
 * Use one of the factory methods to construct an instance:
 * [create], [createOrThrow], or [createOrNull].
 *
 * @property current Current number of trophies.
 * @property highest All-time highest number of trophies.
 */
@ConsistentCopyVisibility
data class PlayerTrophies private constructor(
    val current: Trophies,
    val highest: Trophies,
) {
    companion object {
        private const val ERROR = "Player highest can't be less than current."

        /**
         * Returns `true` if [current] is less than or equal to [highest].
         */
        fun isValid(current: Trophies, highest: Trophies): Boolean =
            current <= highest

        /**
         * Creates a [PlayerTrophies] instance if [current] ≤ [highest], or returns a failed [Result].
         */
        fun create(current: Trophies, highest: Trophies): Result<PlayerTrophies> =
            if (isValid(current, highest)) Result.success(PlayerTrophies(current, highest))
            else Result.failure(IllegalArgumentException(ERROR))

        /**
         * Creates a [PlayerTrophies] instance or throws if [current] > [highest].
         */
        fun createOrThrow(current: Trophies, highest: Trophies): PlayerTrophies =
            create(current, highest).getOrThrow()

        /**
         * Creates a [PlayerTrophies] instance or returns `null` if [current] > [highest].
         */
        fun createOrNull(current: Trophies, highest: Trophies): PlayerTrophies? =
            create(current, highest).getOrNull()
    }
}

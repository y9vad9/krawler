package krawler.server.player.application

import krawler.server.player.application.PlayerRecords.Companion.create
import krawler.server.player.application.PlayerRecords.Companion.createOrNull
import krawler.server.player.application.PlayerRecords.Companion.createOrThrow
import kotlin.time.Duration

/**
 * Represents a Brawl Stars player's personal best performance durations
 * in special timed game modes.
 *
 * These records reflect maximum survival time or endurance in PvE and asymmetrical modes:
 *
 * - [bestRoboRumbleTime]: Longest survival time in **Robo Rumble**, a co-op mode where players
 *   defend a safe from increasingly difficult robot waves.
 *   [Learn more about gamemode](https://brawlstars.fandom.com/wiki/Robo_Rumble).
 * - [bestTimeAsBigBrawler]: Longest time survived as the **Big Brawler** in Big Game,
 *   a mode where one player faces five others as a boss.
 *   [Learn more about gamemode](https://brawlstars.fandom.com/wiki/Big_Game).
 *
 * These values are often used for leaderboard bragging rights or personal stat tracking.
 *
 * Use [create], [createOrThrow], or [createOrNull] to ensure non-negative durations.
 *
 * @property bestRoboRumbleTime Player's best survival duration in Robo Rumble.
 * @property bestTimeAsBigBrawler Player's best duration as the Big Brawler.
 */
@ConsistentCopyVisibility
data class PlayerRecords private constructor(
    val bestRoboRumbleTime: Duration,
    val bestTimeAsBigBrawler: Duration,
) {
    companion object {
        private const val ERROR = "Durations must be positive."

        /**
         * Validates that both durations are non-negative.
         *
         * @param bestRoboRumbleTime Best Robo Rumble duration.
         * @param bestTimeAsBigBrawler Best Big Brawler duration.
         * @return `true` if both durations are non-negative, `false` otherwise.
         */
        fun isValid(bestRoboRumbleTime: Duration, bestTimeAsBigBrawler: Duration): Boolean =
            bestRoboRumbleTime.isPositive() && bestTimeAsBigBrawler.isPositive()

        /**
         * Creates a [PlayerRecords] instance if both durations are valid (non-negative).
         *
         * @param roboRumble Best Robo Rumble duration.
         * @param bigBrawler Best Big Brawler duration.
         * @return A [Result.success] with [PlayerRecords] if valid, or
         *         [Result.failure] with [IllegalArgumentException] if either is negative.
         */
        fun create(
            roboRumble: Duration,
            bigBrawler: Duration
        ): Result<PlayerRecords> =
            if (isValid(roboRumble, bigBrawler)) {
                Result.success(PlayerRecords(roboRumble, bigBrawler))
            } else {
                Result.failure(IllegalArgumentException(ERROR))
            }

        /**
         * Creates a [PlayerRecords] instance or throws [IllegalArgumentException] if invalid.
         *
         * @param roboRumble Best Robo Rumble duration.
         * @param bigBrawler Best Big Brawler duration.
         * @return A valid [PlayerRecords] instance.
         * @throws IllegalArgumentException if either duration is negative.
         */
        fun createOrThrow(
            roboRumble: Duration,
            bigBrawler: Duration
        ): PlayerRecords = create(roboRumble, bigBrawler).getOrThrow()

        /**
         * Creates a [PlayerRecords] instance or returns `null` if validation fails.
         *
         * @param roboRumble Best Robo Rumble duration.
         * @param bigBrawler Best Big Brawler duration.
         * @return A [PlayerRecords] instance, or `null` if invalid.
         */
        fun createOrNull(
            roboRumble: Duration,
            bigBrawler: Duration
        ): PlayerRecords? = create(roboRumble, bigBrawler).getOrNull()
    }
}

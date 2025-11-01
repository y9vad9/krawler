package krawler.server.player.application

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
 * @property bestRoboRumbleTime Player's best survival duration in Robo Rumble.
 * @property bestTimeAsBigBrawler Player's best duration as the Big Brawler.
 */
data class PlayerRecords(
    val bestRoboRumbleTime: Duration,
    val bestTimeAsBigBrawler: Duration,
) {
    init {
        require(bestRoboRumbleTime.isPositive()) {
            "bestRoboRumbleTime should be positive"
        }

        require(bestTimeAsBigBrawler.isPositive()) {
            "bestTimeAsBigBrawler should be positive"
        }
    }
}

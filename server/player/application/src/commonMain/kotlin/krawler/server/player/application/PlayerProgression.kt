package krawler.server.player.application

/**
 * Represents a Brawl Stars player's overall progression status.
 *
 * This data class aggregates key metrics that reflect how far
 * a player has advanced in the game, both in competitive and general play.
 *
 * @property trophies The player's trophy-related progress, including current
 * and highest trophy counts. Trophies are a major progression and matchmaking factor.
 *
 * @property rankedStage The player's current **Ranked** league stage, used
 * in Power League or Club League to determine competitive standing.
 * See: [https://brawlstars.fandom.com/wiki/Ranked]
 *
 * @property experience The player's overall experience level, including
 * experience points and level. Experience increases with every match and unlocks profile features.
 *
 * @property victories The player's victories per category.
 *
 * @see krawler.server.player.application.PlayerTrophies
 * @see PlayerRankedStage
 * @see krawler.server.player.application.PlayerExperience
 * @see krawler.server.player.application.PlayerVictories
 */
data class PlayerProgression(
    val trophies: PlayerTrophies,
    val rankedStage: PlayerRankedStage,
    val experience: PlayerExperience,
    val victories: PlayerVictories,
)

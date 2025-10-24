package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Represents metadata for a specific level in certain battle types, such as Last Stand.
 *
 * This data class encapsulates information about the level's name and identifier,
 * which are used to distinguish between different stages or difficulties within the battle.
 *
 * @property name The name of the level, which may describe its theme or difficulty.
 * @property id A unique identifier for the level, used for referencing and distinguishing
 *     between different levels programmatically.
 */
@Serializable
data class RawBotsBattleLevel(
    val name: String? = null,
    val id: Int? = null,
)

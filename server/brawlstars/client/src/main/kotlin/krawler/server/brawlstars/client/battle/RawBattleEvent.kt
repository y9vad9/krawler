package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Represents the event metadata for a battle in Brawl Stars.
 *
 * This includes optional information about the event such as its unique identifier,
 * the game mode played, and the map where the battle took place.
 *
 * All fields are nullable because in some cases (e.g., battles with maps made with mapmaker),
 * this information may be partially or fully absent.
 *
 * @property id The unique identifier of the event, if available.
 * @property mode The name of the game mode in which the battle was played, if available.
 * @property map The name of the map where the battle took place, if available.
 */
@Serializable
data class RawBattleEvent(
    val id: Int? = null,
    val mode: String? = null,
    val map: String? = null,
)

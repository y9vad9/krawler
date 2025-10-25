package krawler.server.brawlstars.client.battle

import kotlinx.serialization.Serializable

/**
 * Represents a minimal snapshot of a brawler as found inside battle payloads.
 *
 * @property id The numeric brawler ID (e.g., 16000000).
 * @property name The human-readable name of the brawler.
 * @property power The brawler's power level. Can be -1 for bots or unknown.
 * @property trophies The number of trophies on the brawler at the time of the match.
 *                    Can be -1 if it’s a friendly battle or unknown.
 *                    In ranked game modes, this field can also represent the ranked stage.
 *                    Can be null if unknown.
 * @property trophyChange The number of trophies to add/subtract from brawler's [trophies].
 */
@Serializable
data class RawBattleBrawler(
    val id: Int,
    val name: String,
    val power: Int,
    val trophies: Int,
    val trophyChange: Int? = null,
)

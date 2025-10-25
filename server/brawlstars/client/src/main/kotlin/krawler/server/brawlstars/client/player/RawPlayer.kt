package krawler.server.brawlstars.client.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a player's profile in Brawl Stars, encapsulating personal information,
 * progression stats, achievements, and club affiliation.
 *
 * This data class provides a comprehensive overview of a player's in-game identity,
 * including their performance metrics across various game modes, brawler statistics,
 * and social connections.
 *
 * @property tag A unique identifier for the player, typically in the format `#XXXXXX`.
 * @property name The player's chosen display name within the game.
 * @property nameColor The color code associated with the player's name, used for visual
 *     customization in the player list.
 * @property icon The player's profile icon, represented by a [RawPlayerIcon] object.
 * @property trophies The total number of trophies the player has accumulated across all
 *     brawlers.
 * @property highestTrophies The highest number of trophies the player has achieved with
 *     any single brawler.
 * @property expLevel The player's experience level, indicating overall progression.
 * @property expPoints The total experience points the player has earned towards their
 *     current experience level.
 * @property isQualifiedFromChampionshipChallenge A boolean indicating whether the player
 *     has qualified through the Championship Challenge, a competitive event in Brawl Stars.
 * @property threeVsThreeVictories The total number of victories the player has achieved in
 *     3v3 game modes.
 * @property soloVictories The total number of victories the player has achieved in solo
 *     game modes.
 * @property duoVictories The total number of victories the player has achieved in duo
 *     game modes.
 * @property bestRoboRumbleTime The player's best time achieved in the Robo Rumble event,
 *     measured in seconds.
 * @property bestTimeAsBigBrawler The player's best time spent as the Big Brawler in the
 *     Big Game mode, measured in seconds.
 * @property club The player's club affiliation, represented by a [RawPlayerClub] object.
 * @property brawlers A list of the player's brawlers, each represented by a [RawPlayerBrawler]
 *     object, detailing individual brawler statistics and achievements.
 */
@Serializable
data class RawPlayer(
    val tag: String,
    val name: String,
    val nameColor: String,
    val icon: RawPlayerIcon,
    val trophies: Int,
    val highestTrophies: Int,
    val expLevel: Int,
    val expPoints: Int,
    val isQualifiedFromChampionshipChallenge: Boolean,
    @SerialName("3vs3Victories")
    val threeVsThreeVictories: Int,
    val soloVictories: Int,
    val duoVictories: Int,
    val bestRoboRumbleTime: Int,
    val bestTimeAsBigBrawler: Int,
    val club: RawPlayerClub,
    val brawlers: List<RawPlayerBrawler>
)

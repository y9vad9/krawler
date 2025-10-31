package krawler.server.player.application

/**
 * Represents the core details of a Brawl Stars club.
 *
 * @property tag The validated club tag, uniquely identifying the club in the Brawl Stars ecosystem.
 * @property name The human-readable name of the club.
 *
 * While club names and tags are both visible in-game, only the tag is guaranteed unique. Team names may repeat.
 * Use this class to carry club identity within application logic in a type-safe manner.
 */
data class PlayerClub(
    val tag: PlayerClubTag,
    val name: PlayerClubName,
)

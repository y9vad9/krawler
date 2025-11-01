package krawler.server.player.application

import krawler.server.player.application.battle.Battle

/**
 * Represents a player in Brawl Stars, including their metadata, progression, and roster.
 *
 * This class models the top-level state of a player, capturing identity, progression,
 * challenge status, club affiliation, and their owned brawlers.
 *
 * @property tag The unique player identifier (e.g., "#ABCD1234").
 * @property name The display name of the player.
 * @property nameColor Color in the HEX format that is used for [name].
 * @property progression Overall progression details such as total trophies, experience, etc.
 * @property championship The player's status in the Championship Challenge.
 * @property records Personal bests and time-based statistics for special game modes.
 * @property club Information about the club the player belongs to.
 * @property brawlers Summarized information about player's brawlers.
 * @property lastGames Last 3 games that player has played.
 */
data class Player(
    val tag: PlayerTag,
    val name: PlayerName,
    val nameColor: PlayerHexColor,
    val progression: PlayerProgression,
    val championship: PlayerChampionshipQualification,
    val records: PlayerRecords,
    val club: PlayerClub,
    val brawlers: PlayerBrawlersView,
    val lastGames: List<Battle>,
)

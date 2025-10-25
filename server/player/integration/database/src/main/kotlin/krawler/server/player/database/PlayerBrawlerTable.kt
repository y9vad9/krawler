package krawler.server.player.database

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

/**
 * Stores per-brawler statistics for individual players retrieved from the Brawl Stars API.
 *
 * Each row represents the current state of a specific brawler owned by a player,
 * including power level, trophies, and equipped items.
 *
 * The combination of [playerTag] and [brawlerId] serves as a composite primary key,
 * uniquely identifying a player–brawler pair.
 */
class PlayerBrawlerTable(config: PlayerDatabaseConfig) : Table("player_brawler_table") {

    /** Unique identifier of the player (e.g., "#ABCD1234"). Indexed for efficient lookup. */
    val playerTag: Column<String> = varchar("player_tag", config.playerTagMaxLength)
        .index("idx_player_brawler_player_tag")

    /** Numeric identifier of the brawler. Indexed to support brawler-level queries. */
    val brawlerId: Column<Int> = integer("brawler_id")
        .index("idx_player_brawler_id")

    /** Current upgrade level of the brawler. */
    val powerLevel: Column<Int> = integer("power_level")

    /** Current rank of the brawler. */
    val rank: Column<Int> = integer("rank")

    /** Current number of trophies earned with this brawler. */
    val trophies: Column<Int> = integer("trophies")

    /** Highest number of trophies ever achieved with this brawler. */
    val highestTrophies: Column<Int> = integer("highest_trophies")

    /** List of gear IDs currently equipped on this brawler. */
    val gears: Column<List<Int>> = array("gears")

    /** List of unlocked star power IDs for this brawler. */
    val starPowers: Column<List<Int>> = array("star_powers")

    /** List of unlocked gadget IDs for this brawler. */
    val gadgets: Column<List<Int>> = array("gadgets")

    /** Current active win streak, if available. */
    val currentWinStreak: Column<Int?> = integer("current_win_streak").nullable()

    /** Highest recorded win streak, if available. */
    val highestWinStreak: Column<Int?> = integer("highest_win_streak").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(arrayOf(playerTag, brawlerId))
}

/**
 * Represents a player's individual brawler record stored in the database.
 *
 * Each instance corresponds to one row in [PlayerBrawlerTable], capturing
 * a player's current and historical brawler-specific stats.
 *
 * @property playerTag Unique identifier of the player.
 * @property brawlerId Numeric identifier of the brawler.
 * @property powerLevel Current upgrade level of the brawler.
 * @property rank Current rank of the brawler.
 * @property trophies Number of trophies earned with this brawler.
 * @property highestTrophies Highest trophy count ever reached with this brawler.
 * @property gears List of equipped gear IDs.
 * @property starPowers List of unlocked star power IDs.
 * @property gadgets List of unlocked gadget IDs.
 * @property currentWinStreak Current active win streak, if available.
 * @property highestWinStreak Highest recorded win streak, if available.
 */
data class DbPlayerBrawler(
    val playerTag: String,
    val brawlerId: Int,
    val powerLevel: Int,
    val rank: Int,
    val trophies: Int,
    val highestTrophies: Int,
    val gears: List<Int>,
    val starPowers: List<Int>,
    val gadgets: List<Int>,
    val currentWinStreak: Int?,
    val highestWinStreak: Int?,
)

/**
 * Maps a [ResultRow] from [PlayerBrawlerTable] into a [DbPlayerBrawler] DTO.
 */
internal fun ResultRow.toDbPlayerBrawler(table: PlayerBrawlerTable): DbPlayerBrawler = DbPlayerBrawler(
    playerTag = this[table.playerTag],
    brawlerId = this[table.brawlerId],
    powerLevel = this[table.powerLevel],
    rank = this[table.rank],
    trophies = this[table.trophies],
    highestTrophies = this[table.highestTrophies],
    gears = this[table.gears],
    starPowers = this[table.starPowers],
    gadgets = this[table.gadgets],
    currentWinStreak = this[table.currentWinStreak],
    highestWinStreak = this[table.highestWinStreak],
)

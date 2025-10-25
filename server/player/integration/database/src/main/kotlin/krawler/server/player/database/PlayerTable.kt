package krawler.server.player.database

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.duration
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Stores general player information retrieved from the Brawl Stars API.
 *
 * Each row represents a single player, capturing both identity information
 * and gameplay statistics such as trophies, ranked stages, victories, and
 * experience points.
 *
 * Nullable fields represent data that may not always be available (e.g., club affiliation or
 * certain best times).
 */
class PlayerTable(config: PlayerDatabaseConfig) : Table("player_table") {

    companion object {
        /** Maximum allowed length for the hex color string of the player name. */
        const val MAX_COLOR_LENGTH = 9
    }

    /** Unique identifier of the player (e.g., "#ABCD1234"). Primary key for upsert. */
    val tag: Column<String> = varchar("tag", config.playerTagMaxLength)

    /** Optional club tag of the player. Indexed with a named index. */
    val clubTag: Column<String?> = varchar("club_tag", config.clubTagMaxLength)
        .nullable()
        .index("idx_player_club_tag")

    /** Display name of the player. */
    val name: Column<String> = varchar("name", config.playerNameMaxLength)

    /** Hex color code associated with the player's name. */
    val nameColor: Column<String> = varchar("name_color", MAX_COLOR_LENGTH)

    /** Total trophies currently held by the player. */
    val currentTrophies: Column<Int> = integer("current_trophies")

    /** Highest trophies the player has ever achieved. */
    val highestTrophies: Column<Int> = integer("highest_trophies")

    /** Current ranked stage of the player. */
    val currentRankedStage: Column<Int?> = integer("current_ranked_stage").nullable()

    /** Highest ranked stage the player has reached. */
    val highestRankedStage: Column<Int?> = integer("max_ranked_stage").nullable()

    /** Total victories in solo game modes. */
    val soloVictories: Column<Int> = integer("solo_victories")

    /** Total victories in duo game modes. */
    val duoVictories: Column<Int> = integer("duo_victories")

    /** Total victories in trio game modes. */
    val trioVictories: Column<Int> = integer("trio_victories")

    /** Total victories in 5v5 game modes. */
    val quintetVictories: Column<Int> = integer("quintet_victories")

    /** Best time achieved in Robo Rumble, if available. */
    val bestRoboRumbleTime: Column<Duration?> = duration("best_robo_rumble_time").nullable()

    /** Best time spent as Big Brawler, if available. */
    val bestTimeAsBigBrawler: Column<Duration?> = duration("best_time_as_big_brawler").nullable()

    /** Player's current experience level. */
    val expLevel: Column<Int> = integer("exp_level")

    /** Total experience points earned by the player. */
    val expPoints: Column<Int> = integer("exp_points")

    /** Number of brawlers the player owns. */
    val brawlersAmount: Column<Int> = integer("brawlers_amount")

    /** ID of the player's current best-performing brawler. */
    val currentBestBrawlerId: Column<Int> = integer("current_best_brawler_id")

    /** ID of the player's current favorite brawler. */
    val currentFavoriteBrawlerId: Column<Int> = integer("current_favorite_brawler_id")

    /** Last actualization time */
    val actualizedAt: Column<Instant> = timestamp("actualized_at")

    override val primaryKey: PrimaryKey = PrimaryKey(tag, name = "pk_player_tag")
}

/**
 * Data transfer object representing a player snapshot stored in the database.
 *
 * Captures identity information, trophies, ranked progress, victories, experience, and brawler info.
 *
 * @property tag Unique identifier of the player (e.g., "#ABCD1234").
 * @property clubTag Optional club tag of the player, if affiliated with a club.
 * @property name Display name of the player.
 * @property nameColor Hex color code associated with the player's name.
 * @property currentTrophies Total trophies currently held by the player.
 * @property highestTrophies Highest trophies the player has ever achieved.
 * @property currentRankedStage Current ranked stage of the player.
 * @property highestRankedStage Highest ranked stage the player has reached.
 * @property soloVictories Total victories in solo game modes.
 * @property duoVictories Total victories in duo game modes.
 * @property trioVictories Total victories in trio game modes.
 * @property quintetVictories Total victories in quintet game modes.
 * @property bestRoboRumbleTime Best time achieved in Robo Rumble, if available.
 * @property bestTimeAsBigBrawler Best time spent as Big Brawler, if available.
 * @property expLevel Player's current experience level.
 * @property expPoints Total experience points earned by the player.
 * @property brawlersAmount Number of brawlers the player owns.
 * @property currentBestBrawlerId ID of the player's current best-performing brawler.
 * @property currentFavoriteBrawlerId ID of the player's current favorite brawler.
 * @property actualizedAt Last actualization time.
 */
data class DbPlayer(
    val tag: String,
    val clubTag: String?,
    val name: String,
    val nameColor: String,
    val currentTrophies: Int,
    val highestTrophies: Int,
    val currentRankedStage: Int?,
    val highestRankedStage: Int?,
    val soloVictories: Int,
    val duoVictories: Int,
    val trioVictories: Int,
    val quintetVictories: Int,
    val bestRoboRumbleTime: Duration?,
    val bestTimeAsBigBrawler: Duration?,
    val expLevel: Int,
    val expPoints: Int,
    val brawlersAmount: Int,
    val currentBestBrawlerId: Int,
    val currentFavoriteBrawlerId: Int,
    val actualizedAt: Instant,
)

/**
 * Maps a [ResultRow] from [PlayerTable] to a [DbPlayer] DTO.
 *
 * @param table The [PlayerTable] instance containing column definitions.
 * @return [DbPlayer] instance with all fields populated from the row.
 */
internal fun ResultRow.toDbPlayer(table: PlayerTable): DbPlayer = DbPlayer(
    tag = this[table.tag],
    clubTag = this[table.clubTag],
    name = this[table.name],
    nameColor = this[table.nameColor],
    currentTrophies = this[table.currentTrophies],
    highestTrophies = this[table.highestTrophies],
    currentRankedStage = this[table.currentRankedStage],
    highestRankedStage = this[table.highestRankedStage],
    soloVictories = this[table.soloVictories],
    duoVictories = this[table.duoVictories],
    trioVictories = this[table.trioVictories],
    quintetVictories = this[table.quintetVictories],
    bestRoboRumbleTime = this[table.bestRoboRumbleTime],
    bestTimeAsBigBrawler = this[table.bestTimeAsBigBrawler],
    expLevel = this[table.expLevel],
    expPoints = this[table.expPoints],
    brawlersAmount = this[table.brawlersAmount],
    currentBestBrawlerId = this[table.currentBestBrawlerId],
    currentFavoriteBrawlerId = this[table.currentFavoriteBrawlerId],
    actualizedAt = this[table.actualizedAt],
)

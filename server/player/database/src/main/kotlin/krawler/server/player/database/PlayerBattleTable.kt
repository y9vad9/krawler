package krawler.server.player.database

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.duration
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Stores historical snapshots of player battles retrieved from the Brawl Stars API.
 *
 * The table keeps the full JSON of participants and battle info while also maintaining
 * derived fields for fast queries, such as [participantTags].
 */
class PlayerBattleTable(config: PlayerDatabaseConfig, json: Json) : Table("player_battle") {

    /** Identifier of the game mode in which the battle was played. Indexed for fast queries. */
    val gameModeId: Column<Int> = integer("gamemode_id")
        .index("idx_player_battle_gamemode_id")

    /** Event identifier for this battle. Indexed for fast queries. */
    val eventId: Column<Int> = integer("event_id")
        .index("idx_player_battle_event_id")

    /** Type of the battle. Indexed for fast queries. */
    val battleType: Column<BattleType> = customEnumeration(
        name = "battle_type",
        sql = "battletype",
        fromDb = { value -> BattleType.valueOf(value as String) },
        toDb = { value -> value }
    ).index("idx_player_battle_type")

    /** Tag of the star player in this battle. Indexed for fast queries. */
    val starPlayerTag: Column<String> = varchar("star_player_tag", config.playerTagMaxLength)
        .index("idx_player_battle_star_player_tag")

    /** Teams participating in the battle, stored as JSONB (optional). */
    val teams: Column<List<List<String>>?> = jsonb<List<List<String>>>("battle_teams", json).nullable()

    /** Duration of the battle. */
    val duration: Column<Duration> = duration("battle_duration")

    /** Full participant objects for this battle. */
    val participants: Column<List<TeamPlayer>> = jsonb("battle_participants", json)

    /** Extracted player tags for fast lookup. Indexed with GIN. */
    val participantTags: Column<List<String>> = array<String>("battle_participant_tags")
        .index("idx_player_battle_participant_tags")

    /** Result of the battle. */
    val battleResult: Column<BattleResult> = customEnumeration(
        name = "battle_result",
        sql = "battleresult",
        fromDb = { value -> BattleResult.valueOf(value as String) },
        toDb = { value -> value }
    )

    /** Timestamp when the battle occurred. */
    val battleTime: Column<Instant> = timestamp("battle_time")
        .index("idx_player_battle_time")

    /** Timestamp when this record was fetched from the API. */
    val fetchTime: Column<Instant> = timestamp("fetch_time")

    override val primaryKey: PrimaryKey = PrimaryKey(arrayOf(starPlayerTag, battleTime))
}

/**
 * Represents a player participating in a battle.
 *
 * @property playerTag Unique identifier of the player (e.g., "#XXXXXX").
 * @property name Display name of the player.
 * @property power Current power level of the player.
 * @property brawler The brawler used by the player in this battle.
 * @property trophyChange Change in trophies as a result of this battle, if applicable.
 */
@Serializable
data class TeamPlayer(
    val playerTag: String,
    val name: String,
    val power: Int,
    val brawler: Brawler,
    val trophyChange: Int? = null,
) {

    /**
     * Represents the brawler used by a player in a battle.
     *
     * @property id Unique identifier of the brawler.
     * @property power Current power level of the brawler.
     * @property trophies Number of trophies the brawler has earned, if applicable.
     * @property rankedStage Ranked stage of the brawler, if applicable.
     */
    @Serializable
    data class Brawler(
        val id: Int,
        val power: Int,
        val trophies: Int? = null,
        val rankedStage: Int? = null,
    )
}

/**
 * DTO representing a historical snapshot of a player battle.
 *
 * @property gameModeId Identifier of the game mode in which the battle was played.
 * @property eventId Identifier of the event in which the battle occurred.
 * @property battleType Type of the battle.
 * @property starPlayerTag Tag of the star player.
 * @property teams Optional teams information stored as nested arrays.
 * @property duration Duration of the battle.
 * @property participants Full participant data for the battle.
 * @property battleResult Result of the battle.
 * @property battleTime Timestamp when the battle occurred.
 * @property fetchTime Timestamp when the record was fetched.
 */
data class DbPlayerBattle(
    val gameModeId: Int,
    val eventId: Int,
    val battleType: BattleType,
    val starPlayerTag: String,
    val teams: List<List<String>>?,
    val duration: Duration,
    val participants: List<TeamPlayer>,
    val battleResult: BattleResult,
    val battleTime: Instant,
    val fetchTime: Instant
)

/**
 * Maps an Exposed ResultRow from [PlayerBattleTable] to a [DbPlayerBattle].
 */
internal fun ResultRow.toDbPlayerBattle(table: PlayerBattleTable): DbPlayerBattle = DbPlayerBattle(
    gameModeId = this[table.gameModeId],
    eventId = this[table.eventId],
    battleType = this[table.battleType],
    starPlayerTag = this[table.starPlayerTag],
    teams = this[table.teams],
    duration = this[table.duration],
    participants = this[table.participants],
    battleResult = this[table.battleResult],
    battleTime = this[table.battleTime],
    fetchTime = this[table.fetchTime]
)

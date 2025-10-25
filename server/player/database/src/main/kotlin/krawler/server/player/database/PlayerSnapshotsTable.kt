package krawler.server.player.database

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb
import kotlin.time.Instant

/**
 * Stores historical snapshots of players retrieved from the Brawl Stars API.
 *
 * Each row captures a full API response along with metadata for historical analysis.
 */
class PlayerSnapshotsTable(json: Json, config: PlayerDatabaseConfig) : Table("player_snapshot_table") {

    /** Unique identifier of the player. Indexed for fast lookup. */
    val tag: Column<String> = varchar("tag", config.playerTagMaxLength)
        .index("idx_player_snapshot_table_tag")

    /** Tag of the player's club, if any. Indexed for fast queries by club. */
    val clubTag: Column<String?> = varchar("club_tag", config.clubTagMaxLength)
        .index("idx_player_snapshot_table_club_tag")
        .nullable()

    /** Full JSON payload returned by the API, stored as JSONB for historical tracking. */
    val responseJson: Column<JsonObject> = jsonb("response_json", json)

    /** Timestamp when this record was fetched from the API. Defaults to UTC now. */
    val fetchTime: Column<Instant> = timestamp("fetch_time")

    override val primaryKey: PrimaryKey = PrimaryKey(arrayOf(tag, fetchTime))
}

/**
 * Data transfer object representing a historical player snapshot.
 *
 * @property tag Unique identifier of the player.
 * @property clubTag Tag of the player's club, if any.
 * @property responseJson Full API response as JSON.
 * @property fetchTime Timestamp when the data was fetched.
 */
data class DbPlayerSnapshot(
    val tag: String,
    val clubTag: String?,
    val responseJson: JsonObject,
    val fetchTime: Instant
)

/**
 * Maps an Exposed ResultRow from PlayerTable to a PlayerDto.
 */
internal fun ResultRow.toDbPlayer(table: PlayerSnapshotsTable): DbPlayerSnapshot = DbPlayerSnapshot(
    tag = this[table.tag],
    clubTag = this[table.clubTag],
    responseJson = this[table.responseJson],
    fetchTime = this[table.fetchTime]
)

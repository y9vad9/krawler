package krawler.server.brawlstars.database

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.time.Instant

/**
 * Stores historical snapshots of clubs retrieved from the Brawl Stars API.
 *
 * Each row captures a full API response along with metadata for historical analysis.
 */
class ClubSnapshotsTable(
    json: Json,
    config: BrawlStarsDatabaseConfig,
    private val database: R2dbcDatabase,
) : Table("club_snapshot_table") {

    /** Unique identifier of the club. Indexed for fast lookup. */
    val tag: Column<String> = varchar("tag", config.clubTagMaxLength).index("idx_club_table_tag")

    /** Full JSON payload returned by the API, stored as JSONB for historical tracking. */
    val responseJson: Column<JsonObject> = jsonb("response_json", json)

    /** Timestamp when this record was fetched from the API. Defaults to UTC now. */
    val fetchTime: Column<Instant> = timestamp("fetch_time")

    /**
     * Inserts multiple club snapshots in a single bulk operation.
     *
     * @param snapshots List of [DbClubSnapshot] objects to insert.
     */
    suspend fun insertBulk(snapshots: List<DbClubSnapshot>) = suspendTransaction(database) {
        batchInsert(snapshots) { snapshot ->
            this[tag] = snapshot.tag
            this[responseJson] = snapshot.responseJson
            this[fetchTime] = snapshot.fetchTime
        }
    }

    /**
     * Inserts a new snapshot for a club.
     */
    suspend fun insert(tagValue: String, json: JsonObject, fetchTime: Instant) = suspendTransaction(database) {
        insert {
            it[tag] = tagValue
            it[responseJson] = json
            it[this.fetchTime] = fetchTime
        }
    }

    /**
     * Retrieves all snapshots for a club within the specified time range.
     */
    suspend fun selectInRange(
        clubTag: String,
        from: Instant,
        to: Instant,
    ): List<ResultRow> = suspendTransaction(database) {
        selectAll().where {
            (tag eq clubTag) and (fetchTime greaterEq from) and (fetchTime lessEq to)
        }.orderBy(fetchTime to SortOrder.ASC).toList()
    }

    /**
     * Retrieves the most recent snapshot for a club.
     */
    suspend fun selectLast(clubTag: String): ResultRow? = suspendTransaction(database) {
        selectAll().where { tag eq clubTag }
            .orderBy(fetchTime to SortOrder.DESC)
            .limit(1)
            .firstOrNull()
    }
}

/**
 * Data transfer object representing a historical club snapshot.
 *
 * @property tag Unique identifier of the club.
 * @property responseJson Full API response as JSON.
 * @property fetchTime Timestamp when the data was fetched.
 */
data class DbClubSnapshot(
    val tag: String,
    val responseJson: JsonObject,
    val fetchTime: Instant
)

/**
 * Maps an Exposed ResultRow from ClubTable to a DbClubSnapshot.
 */
internal fun ResultRow.toDbClubSnapshot(table: ClubSnapshotsTable): DbClubSnapshot = DbClubSnapshot(
    tag = this[table.tag],
    responseJson = this[table.responseJson],
    fetchTime = this[table.fetchTime]
)

package krawler.server.player.database

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import krawler.exposed.suspendReadTransaction
import krawler.exposed.suspendWriteTransaction
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import kotlin.time.Instant

/**
 * Data access layer for storing and retrieving historical player data and battle snapshots
 * from the Brawl Stars API.
 *
 * @param config Configuration values that define schema constraints for player tables.
 * @param json Shared JSON serializer used for storing and retrieving payloads.
 * @param database Reactive database connection instance.
 */
class PlayerSnapshotsDataSource(
    config: PlayerDatabaseConfig,
    json: Json,
    private val database: R2dbcDatabase,
) {
    private val playerSnapshots = PlayerSnapshotsTable(json, config)
    private val battleSnapshots = PlayerBattleSnapshotsTable(json, config)

    /**
     * Retrieves the most recent player snapshot for the given player [tag].
     *
     * @param tag Unique player tag.
     * @return The latest [DbPlayerSnapshot] if found, or `null` otherwise.
     */
    suspend fun getLastPlayerRecord(tag: String): DbPlayerSnapshot? = suspendReadTransaction(database) {
        playerSnapshots.selectAll()
            .where { playerSnapshots.tag eq tag }
            .orderBy(playerSnapshots.fetchTime, SortOrder.DESC)
            .firstOrNull()
            ?.toDbPlayer(playerSnapshots)
    }

    /**
     * Retrieves all player snapshots for the given [tag] within the specified [timeline].
     *
     * @param tag Unique player tag.
     * @param timeline Inclusive time range to filter snapshots.
     * @return List of [DbPlayerSnapshot]s or `null` if none exist in the range.
     */
    suspend fun getPlayerRecords(tag: String, timeline: ClosedRange<Instant>): List<DbPlayerSnapshot>? =
        suspendReadTransaction(database) {
            playerSnapshots.selectAll()
                .where {
                    (playerSnapshots.tag eq tag)
                        .and(playerSnapshots.fetchTime greaterEq timeline.start)
                        .and(playerSnapshots.fetchTime lessEq timeline.endInclusive)
                }
                .orderBy(playerSnapshots.fetchTime, SortOrder.DESC)
                .map { it.toDbPlayer(playerSnapshots) }
                .toList()
                .takeUnless { it.isEmpty() }
        }

    /**
     * Inserts a single player snapshot into the database.
     *
     * @param snapshot Player snapshot to insert.
     */
    suspend fun insertPlayerRecord(snapshot: DbPlayerSnapshot) = suspendWriteTransaction(database) {
        playerSnapshots.insert {
            it[playerSnapshots.tag] = snapshot.tag
            it[playerSnapshots.clubTag] = snapshot.clubTag
            it[playerSnapshots.fetchTime] = snapshot.fetchTime
            it[playerSnapshots.responseJson] = snapshot.responseJson
        }
    }

    /**
     * Inserts multiple player snapshots in a single bulk operation.
     *
     * Uses `batchInsert` to perform a true bulk insert, significantly improving performance
     * for large historical imports.
     *
     * @param snapshots List of [DbPlayerSnapshot]s to insert.
     */
    suspend fun insertPlayerRecords(snapshots: List<DbPlayerSnapshot>) = suspendWriteTransaction(database) {
        // TODO change to batchInsert once
        //  https://youtrack.jetbrains.com/issue/EXPOSED-912/Exposed-upsert-does-not-work-with-nullable-fields-in-R2DBC
        // is resolved
        snapshots.forEach { snapshot ->
            playerSnapshots.insert {
                it[playerSnapshots.tag] = snapshot.tag
                if (snapshot.clubTag != null) {
                    it[playerSnapshots.clubTag] = snapshot.clubTag
                }
                it[playerSnapshots.fetchTime] = snapshot.fetchTime
                it[playerSnapshots.responseJson] = snapshot.responseJson
            }
        }
    }

    /**
     * Retrieves all battle snapshots for a given player [tag] within the specified [timeline].
     *
     * @param tag Player tag identifying the player.
     * @param timeline Inclusive time range to filter battles by.
     * @return List of [DbPlayerBattleSnapshot]s or `null` if none exist.
     */
    suspend fun getPlayerBattleRecords(
        tag: String,
        timeline: ClosedRange<Instant>,
    ): List<DbPlayerBattleSnapshot>? = suspendReadTransaction(database) {
        battleSnapshots.selectAll()
            .where {
                (battleSnapshots.playerTag eq tag)
                    .and(battleSnapshots.fetchTime greaterEq timeline.start)
                    .and(battleSnapshots.fetchTime lessEq timeline.endInclusive)
            }
            .orderBy(battleSnapshots.battleTime, SortOrder.DESC)
            .map { it.toDbPlayerBattleSnapshot(battleSnapshots) }
            .toList()
            .takeUnless { it.isEmpty() }
    }

    /**
     * Inserts a single player battle snapshot into the database.
     *
     * @param snapshot Battle snapshot to insert.
     */
    suspend fun insertPlayerBattleRecord(snapshot: DbPlayerBattleSnapshot) = suspendWriteTransaction(database) {
        battleSnapshots.insert {
            it[battleSnapshots.playerTag] = snapshot.playerTag
            it[battleSnapshots.battleResult] = snapshot.battleResult
            it[battleSnapshots.fetchTime] = snapshot.fetchTime
            it[battleSnapshots.responseJson] = snapshot.responseJson
            it[battleSnapshots.gameModeId] = snapshot.gameModeId
            it[battleSnapshots.battleTime] = snapshot.battleTime
        }
    }

    /**
     * Inserts multiple player battle snapshots in a bulk operation.
     *
     * Uses `batchInsert` with `ignore = true` since duplicate entries (e.g., overlapping API fetches)
     * are expected and should not cause constraint violations.
     *
     * @param snapshots List of [DbPlayerBattleSnapshot]s to insert.
     */
    suspend fun insertPlayerBattleRecords(snapshots: List<DbPlayerBattleSnapshot>) = suspendWriteTransaction(database) {
        battleSnapshots.batchInsert(snapshots, ignore = true) { snapshot ->
            set(battleSnapshots.playerTag, snapshot.playerTag)
            set(battleSnapshots.battleResult, snapshot.battleResult)
            set(battleSnapshots.fetchTime, snapshot.fetchTime)
            set(battleSnapshots.battleTime, snapshot.battleTime)
            set(battleSnapshots.gameModeId, snapshot.gameModeId)
            set(battleSnapshots.responseJson, snapshot.responseJson)
        }
    }
}

package krawler.server.brawlstars.database

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb
import kotlin.time.Instant

/**
 * Stores historical snapshots of player battles retrieved from the Brawl Stars API.
 *
 * Each row captures a single battle along with its result, game mode, full API response,
 * and the fetch timestamp for historical tracking and analytics.
 */
class PlayerBattleSnapshotsTable(json: Json, config: BrawlStarsDatabaseConfig) : Table("player_battle_snapshots") {

    /** Unique identifier of the player. Indexed for fast lookup. */
    val playerTag: Column<String> = varchar("player_tag", config.playerTagMaxLength)
        .index("idx_player_battle_snapshots_player_tag")

    /** Identifier of the game mode in which the battle was played. Indexed for fast queries. */
    val gameModeId: Column<Int> = integer("gamemode_id")
        .index("idx_player_battle_snapshots_gamemode_id")

    /** Result of the battle. */
    val battleResult: Column<BattleResult> = enumeration<BattleResult>("battle_result")

    /** Full JSON payload returned by the API for this battle. */
    val responseJson: Column<JsonObject> = jsonb("response_json", json)

    /** Timestamp when the battle occurred. */
    val battleTime: Column<Instant> = timestamp("battle_time")

    /** Timestamp when this record was fetched from the API. */
    val fetchTime: Column<Instant> = timestamp("fetch_time")
}

/**
 * Enum representing the possible results of a player battle.
 */
enum class BattleResult {
    VICTORY, DEFEAT, DRAW,
}

/**
 * Data transfer object representing a historical player battle snapshot.
 *
 * @property playerTag Unique identifier of the player.
 * @property gameModeId Identifier of the game mode.
 * @property battleResult Result of the battle.
 * @property responseJson Full API response as JSON.
 * @property battleTime Timestamp when the battle occurred.
 * @property fetchTime Timestamp when this record was fetched.
 */
data class DbPlayerBattleSnapshot(
    val playerTag: String,
    val gameModeId: Int,
    val battleResult: BattleResult,
    val responseJson: JsonObject,
    val battleTime: Instant,
    val fetchTime: Instant
)

/**
 * Maps an Exposed ResultRow from PlayerBattleSnapshotsTable to a DbPlayerBattleSnapshot.
 */
internal fun ResultRow.toDbPlayerBattleSnapshot(table: PlayerBattleSnapshotsTable): DbPlayerBattleSnapshot =
    DbPlayerBattleSnapshot(
        playerTag = this[table.playerTag],
        gameModeId = this[table.gameModeId],
        battleResult = this[table.battleResult],
        responseJson = this[table.responseJson],
        battleTime = this[table.battleTime],
        fetchTime = this[table.fetchTime]
    )

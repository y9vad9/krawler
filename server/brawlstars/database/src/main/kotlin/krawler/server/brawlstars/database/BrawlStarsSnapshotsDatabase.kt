package krawler.server.brawlstars.database

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

class BrawlStarsSnapshotsDatabase(
    private val database: R2dbcDatabase,
    private val json: Json,
    private val config: BrawlStarsDatabaseConfig,
) {
    private val playerSnapshots: PlayerSnapshotsTable = PlayerSnapshotsTable(json, config)
    private val clubSnapshots: ClubSnapshotsTable = ClubSnapshotsTable(json, config, database)
    private val playerBattleSnapshots: PlayerBattleSnapshotsTable = PlayerBattleSnapshotsTable(json, config)

    suspend fun
}

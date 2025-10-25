package krawler.server.player.database

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import krawler.exposed.suspendReadTransaction
import krawler.exposed.suspendWriteTransaction
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.anyFrom
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.stringParam
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.batchUpsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.upsert
import kotlin.time.Instant

/**
 * Provides access to player-related data in the Brawl Stars database.
 *
 * This class is responsible for reading and writing normalized player information,
 * including general player stats, per-brawler stats, and historical battle records.
 * All operations use R2DBC transactions for reactive database access.
 *
 * @param database The R2DBC database instance used for all operations.
 * @param config Config with restrictions on fields, like player tag.
 * @param json Json that is used for JSONB columns.
 */
class PlayerDataSource(
    private val database: R2dbcDatabase,
    config: PlayerDatabaseConfig,
    json: Json,
) {
    private val brawlerTable = PlayerBrawlerTable(config)
    private val playerTable = PlayerTable(config)
    private val battleTable = PlayerBattleTable(config, json)


    /**
     * Retrieves a single player by tag.
     *
     * Optionally filters by [afterActualization], returning only a player
     * whose `actualizedAt` timestamp is equal or later than the specified time.
     *
     * @param tag The player's unique tag.
     * @param afterActualization Optional timestamp to filter outdated data.
     * @return The player as a [DbPlayer], or null if not found.
     */
    suspend fun getPlayer(
        tag: String,
        afterActualization: Instant? = null,
    ): DbPlayer? = suspendReadTransaction(database) {
        playerTable.selectAll()
            .where {
                if (afterActualization == null)
                    playerTable.tag eq tag
                else (playerTable.tag eq tag) and (playerTable.actualizedAt greaterEq afterActualization)
            }
            .singleOrNull()
            ?.toDbPlayer(playerTable)
    }


    /**
     * Inserts or updates a single player record in the database.
     *
     * Uses an upsert operation based on the player's tag.
     *
     * @param player Player data to insert or update.
     */
    @Suppress("detekt.CognitiveComplexMethod")
    suspend fun addOrSetPlayer(player: DbPlayer) = suspendWriteTransaction(database) {
        playerTable.upsert(playerTable.tag) {
            it[tag] = player.tag
            if (player.clubTag != null)
                it[clubTag] = player.clubTag
            it[name] = player.name
            it[nameColor] = player.nameColor
            it[currentTrophies] = player.currentTrophies
            it[highestTrophies] = player.highestTrophies
            if (player.currentRankedStage != null)
                it[currentRankedStage] = player.currentRankedStage
            if (player.highestRankedStage != null)
                it[highestRankedStage] = player.highestRankedStage
            it[soloVictories] = player.soloVictories
            it[duoVictories] = player.duoVictories
            it[trioVictories] = player.trioVictories
            it[quintetVictories] = player.quintetVictories
            if (player.bestRoboRumbleTime != null)
                it[bestRoboRumbleTime] = player.bestRoboRumbleTime
            if (player.bestTimeAsBigBrawler != null)
                it[bestTimeAsBigBrawler] = player.bestTimeAsBigBrawler
            it[expLevel] = player.expLevel
            it[expPoints] = player.expPoints
            it[brawlersAmount] = player.brawlersAmount
            it[currentBestBrawlerId] = player.currentBestBrawlerId
            it[currentFavoriteBrawlerId] = player.currentFavoriteBrawlerId
            it[actualizedAt] = player.actualizedAt
        }
    }

    /**
     * Inserts or updates multiple player records in a single batch.
     *
     * Each player is upserted based on the tag.
     *
     * @param players List of players to insert or update.
     */
    @Suppress("detekt.CognitiveComplexMethod")
    suspend fun addOrSetPlayers(players: List<DbPlayer>) = suspendWriteTransaction(database) {
        // TODO change to batchUpsert once
        //  https://youtrack.jetbrains.com/issue/EXPOSED-912/Exposed-upsert-does-not-work-with-nullable-fields-in-R2DBC
        // is resolved
        for (player in players) {
            playerTable.upsert(playerTable.tag) {
                it[tag] = player.tag
                if (player.clubTag != null) it[clubTag] = player.clubTag
                it[name] = player.name
                it[nameColor] = player.nameColor
                it[currentTrophies] = player.currentTrophies
                it[highestTrophies] = player.highestTrophies
                if (player.currentRankedStage != null) it[currentRankedStage] = player.currentRankedStage
                if (player.highestRankedStage != null) it[highestRankedStage] = player.highestRankedStage
                it[soloVictories] = player.soloVictories
                it[duoVictories] = player.duoVictories
                it[trioVictories] = player.trioVictories
                it[quintetVictories] = player.quintetVictories
                if (player.bestRoboRumbleTime != null) it[bestRoboRumbleTime] = player.bestRoboRumbleTime
                if (player.bestTimeAsBigBrawler != null) it[bestTimeAsBigBrawler] = player.bestTimeAsBigBrawler
                it[expLevel] = player.expLevel
                it[expPoints] = player.expPoints
                it[brawlersAmount] = player.brawlersAmount
                it[currentBestBrawlerId] = player.currentBestBrawlerId
                it[currentFavoriteBrawlerId] = player.currentFavoriteBrawlerId
                it[actualizedAt] = player.actualizedAt
            }
        }
    }

    /**
     * Retrieves all brawlers for a given player, ordered by [PlayerBrawlerTable.brawlerId].
     *
     * @param tag Player tag to fetch brawlers for.
     * @return List of [DbPlayerBrawler] records.
     */
    suspend fun getPlayerBrawlers(tag: String): List<DbPlayerBrawler> = suspendReadTransaction(database) {
        brawlerTable.selectAll()
            .where { brawlerTable.playerTag eq tag }
            .orderBy(brawlerTable.brawlerId, SortOrder.ASC)
            .map { it.toDbPlayerBrawler(brawlerTable) }
            .toList()
    }

    /**
     * Inserts or updates multiple player brawlers in a batch.
     *
     * Upsert is performed based on the composite key of [PlayerBrawlerTable.playerTag] and
     * [PlayerBrawlerTable.brawlerId].
     *
     * @param brawlers List of brawler records to insert or update.
     */
    suspend fun addOrSetPlayerBrawlers(brawlers: List<DbPlayerBrawler>) = suspendWriteTransaction(database) {
        brawlerTable.batchUpsert(
            data = brawlers,
            keys = arrayOf(brawlerTable.playerTag, brawlerTable.brawlerId),
        ) { b ->
            this[brawlerTable.brawlerId] = b.brawlerId
            this[brawlerTable.playerTag] = b.playerTag
            this[brawlerTable.powerLevel] = b.powerLevel
            this[brawlerTable.rank] = b.rank
            this[brawlerTable.trophies] = b.trophies
            this[brawlerTable.highestTrophies] = b.highestTrophies
            this[brawlerTable.gears] = b.gears
            this[brawlerTable.starPowers] = b.starPowers
            this[brawlerTable.gadgets] = b.gadgets
            if (b.currentWinStreak != null)
                this[brawlerTable.currentWinStreak] = b.currentWinStreak
            if (b.highestWinStreak != null)
                this[brawlerTable.highestWinStreak] = b.highestWinStreak
        }
    }

    /**
     * Retrieves historical battles in which the specified player participated.
     *
     * Filters by [timeline] for battle times. The participant tags are stored
     * separately for fast lookups.
     *
     * @param tag Player tag to filter battles by.
     * @param timeline Range of battle times to consider.
     * @return List of [DbPlayerBattle] for battles within the specified time range.
     */
    suspend fun getPlayerBattles(
        tag: String,
        timeline: ClosedRange<Instant>,
    ): List<DbPlayerBattle> = suspendReadTransaction(database) {
        battleTable.selectAll()
            .where {
                (stringParam(tag) eq anyFrom(battleTable.participantTags))
                    .and(battleTable.battleTime greaterEq timeline.start)
                    .and(battleTable.battleTime lessEq timeline.endInclusive)
            }
            .orderBy(battleTable.battleTime, SortOrder.DESC)
            .map { it.toDbPlayerBattle(battleTable) }
            .toList()
    }

    /**
     * Inserts multiple historical player battles into the database.
     *
     * Duplicates are ignored to prevent repeated snapshots from causing errors.
     * The [PlayerBattleTable.participantTags] column is derived from [DbPlayerBattle.participants] for fast queries.
     *
     * @param battles List of player battle snapshots to insert.
     */
    suspend fun addOrIgnorePlayerBattles(
        battles: List<DbPlayerBattle>,
    ) = suspendWriteTransaction(database) {
        battleTable.batchInsert(
            data = battles,
            ignore = true,
        ) { battle ->
            this[battleTable.gameModeId] = battle.gameModeId
            this[battleTable.eventId] = battle.eventId
            this[battleTable.battleType] = battle.battleType
            this[battleTable.starPlayerTag] = battle.starPlayerTag
            this[battleTable.teams] = battle.teams
            this[battleTable.duration] = battle.duration
            this[battleTable.participants] = battle.participants
            this[battleTable.participantTags] = battle.participants.map { it.playerTag }
            this[battleTable.battleResult] = battle.battleResult
            this[battleTable.battleTime] = battle.battleTime
            this[battleTable.fetchTime] = battle.fetchTime
        }
    }
}

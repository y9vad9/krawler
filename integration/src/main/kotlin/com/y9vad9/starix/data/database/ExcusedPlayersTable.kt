package com.y9vad9.starix.data.database

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class ExcusedPlayersTable(
    private val database: Database,
) {
    companion object : Table(name = "excused_players") {
        val PLAYER_TAG = varchar("player_tag", PlayerTag.REQUIRED_SIZE + 1)
        val CLUB_TAG = varchar("player_tag", ClubTag.REQUIRED_SIZE + 1)
        val UNTIL_TIME = long("until_time")
        val CREATION_TIME = long("creation_time")
    }

    init {
        transaction(database) {
            SchemaUtils.create(ExcusedPlayersTable)
        }
    }

    suspend fun create(
        playerTag: String,
        clubTag: String,
        untilTime: Long,
        creationTime: Long,
    ): Unit = newSuspendedTransaction(db = database) {
        insert {
            it[PLAYER_TAG] = playerTag
            it[UNTIL_TIME] = untilTime
            it[CREATION_TIME] = creationTime
        }
    }

    suspend fun getListOfTags(
        clubTag: String,
        time: Long,
    ): List<String> = newSuspendedTransaction(db = database) {
        selectAll()
            .where { (CLUB_TAG eq clubTag) and (UNTIL_TIME greaterEq time) }
            .map { it[PLAYER_TAG] }
    }

    suspend fun exists(playerTag: String, time: Long): Boolean = newSuspendedTransaction(db = database) {
        selectAll()
            .where { (PLAYER_TAG eq playerTag) and (UNTIL_TIME lessEq time) }
            .count() > 0
    }
}
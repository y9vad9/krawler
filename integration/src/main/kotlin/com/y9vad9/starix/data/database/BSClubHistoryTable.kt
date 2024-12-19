package com.y9vad9.starix.data.database

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class BSClubHistoryTable(
    private val database: Database,
) {
    companion object : Table() {
        val SAVE_ID = uuid("save_id").autoGenerate()
        val CLUB_TAG = varchar("club_tag", ClubTag.REQUIRED_SIZE + 1)
        val BS_CLUB_JSON = text("bs_club_json")
        val UNIX_TIME = long("unix_time")
    }

    init {
        transaction(database) {
            SchemaUtils.create(BSClubHistoryTable)
        }
    }

    suspend fun create(
        clubTag: String,
        json: String,
        unixTime: Long,
    ): Unit = newSuspendedTransaction(db = database) {
        insert {
            it[CLUB_TAG] = clubTag
            it[BS_CLUB_JSON] = json
            it[UNIX_TIME] = unixTime
        }
    }

    suspend fun getJsonOfLastBefore(
        clubTag: String,
        unixTime: Long,
    ): String? = newSuspendedTransaction(db = database) {
        selectAll().where { (CLUB_TAG eq clubTag) and (UNIX_TIME lessEq unixTime) }
            .orderBy(UNIX_TIME to SortOrder.DESC)
            .firstOrNull()
            ?.get(BS_CLUB_JSON)
    }

    suspend fun getJsonOfFirstAfter(
        clubTag: String,
        unixTime: Long,
    ): String? = newSuspendedTransaction(db = database) {
        selectAll()
            .where { (CLUB_TAG eq clubTag) and (UNIX_TIME greaterEq unixTime) }
            .orderBy(UNIX_TIME to SortOrder.ASC)
            .firstOrNull()
            ?.get(BS_CLUB_JSON)
    }
}
package com.y9vad9.bcm.data.database

import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class BSClubHistoryTable(
    private val database: Database,
) {
    companion object : Table() {
        val SAVE_ID = uuid("save_id").autoGenerate()
        val CLUB_TAG = varchar("club_tag", ClubTag.REQUIRED_SIZE)
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
        select { (CLUB_TAG eq clubTag) and (UNIX_TIME lessEq unixTime) }
            .orderBy(UNIX_TIME to SortOrder.DESC)
            .firstOrNull()
            ?.get(BS_CLUB_JSON)
    }

    suspend fun getJsonOfFirstAfter(
        clubTag: String,
        unixTime: Long,
    ): String? = newSuspendedTransaction(db = database) {
        select { (CLUB_TAG eq clubTag) and (UNIX_TIME greaterEq unixTime) }
            .orderBy(UNIX_TIME to SortOrder.ASC)
            .firstOrNull()
            ?.get(BS_CLUB_JSON)
    }
}
package com.y9vad9.bcm.data.database

import com.y9vad9.bcm.domain.entity.JoinRequest
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class)
class JoinRequestsTable(private val database: Database) {
    companion object : Table(name = "join_requests") {
        val ID = uuid("id").uniqueIndex()
        val TELEGRAM_ID = long("telegram_id")
        val PLAYER_TAG = varchar("player_tag", PlayerTag.REQUIRED_SIZE)
        val CLUB_TAG = varchar("club_tag", ClubTag.REQUIRED_SIZE)
        val MESSAGE = varchar("message", CustomMessage.SIZE_RANGE.last)
        val STATUS = enumeration<Status>("status")
    }

    init {
        transaction(database) {
            SchemaUtils.create(JoinRequestsTable)
        }
    }

    suspend fun create(
        id: Uuid,
        playerTag: String,
        clubTag: String,
        tgId: Long,
        message: String,
    ) = newSuspendedTransaction(db = database) {
        insert {
            it[ID] = id.toJavaUuid()
            it[PLAYER_TAG] = playerTag
            it[CLUB_TAG] = clubTag
            it[TELEGRAM_ID] = tgId
            it[MESSAGE] = message
        }.resultedValues!!.first().toJoinRequest()
    }

    suspend fun get(id: Uuid): JoinRequest? = newSuspendedTransaction(db = database) {
        select { ID eq id.toJavaUuid() }.firstOrNull()?.toJoinRequest()
    }

    suspend fun count(status: Status): Long = newSuspendedTransaction(db = database) {
        select { STATUS eq status }.count()
    }

    suspend fun getList(
        status: Status,
        size: Int,
    ): List<JoinRequest> = newSuspendedTransaction(db = database) {
        select { STATUS eq status }.limit(size, 0).map(ResultRow::toJoinRequest)
    }

    suspend fun updateStatus(id: Uuid, status: Status) = newSuspendedTransaction(db = database) {
        update(where = { ID eq id.toJavaUuid() }) {
            it[STATUS] = status
        }
    }

    suspend fun delete(id: Uuid) = newSuspendedTransaction(db = database) {
        deleteWhere { ID eq id.toJavaUuid() }
    }

    suspend fun deleteByTelegramId(tgId: Long) = newSuspendedTransaction(db = database) {
        deleteWhere { TELEGRAM_ID eq id }
    }

    suspend fun hasAnyFromTgId(id: Long) = newSuspendedTransaction(db = database) {
        !select { TELEGRAM_ID eq id }.empty()
    }

    enum class Status {
        UNDECIDED, ACCEPTED, DECLINED,
    }
}

@OptIn(ExperimentalUuidApi::class, ValidationDelicateApi::class)
private fun ResultRow.toJoinRequest(): JoinRequest {
    return JoinRequest(
        id = get(JoinRequestsTable.ID).toKotlinUuid(),
        playerTag = PlayerTag.createUnsafe(get(JoinRequestsTable.PLAYER_TAG)),
        clubTag = ClubTag.createUnsafe(get(JoinRequestsTable.CLUB_TAG)),
        message = CustomMessage.createUnsafe(get(JoinRequestsTable.MESSAGE)),
        tgId = TelegramUserId.createUnsafe(get(JoinRequestsTable.TELEGRAM_ID)),
    )
}
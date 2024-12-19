package com.y9vad9.starix.data.database

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.system.entity.JoinRequest
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class)
class JoinRequestsTable(private val database: Database) {
    companion object : Table(name = "join_requests") {
        val ID = uuid("id").uniqueIndex()
        val TELEGRAM_ID = long("telegram_id")
        val PLAYER_TAG = varchar("player_tag", PlayerTag.REQUIRED_SIZE + 1)
        val CLUB_TAG = varchar("club_tag", ClubTag.REQUIRED_SIZE + 1)
        val MESSAGE = varchar("message", CustomMessage.SIZE_RANGE.last)
        val STATUS = enumeration<Status>("status").default(Status.UNDECIDED)
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
        selectAll().where { ID eq id.toJavaUuid() }.firstOrNull()?.toJoinRequest()
    }

    suspend fun count(status: Status): Long = newSuspendedTransaction(db = database) {
        selectAll().where { STATUS eq status }.count()
    }

    suspend fun getList(
        status: Status,
        size: Int,
    ): List<JoinRequest> = newSuspendedTransaction(db = database) {
        selectAll()
            .where { STATUS eq status }
            .limit(size)
            .offset(0)
            .map(ResultRow::toJoinRequest)
    }

    suspend fun updateStatus(id: Uuid, status: Status) = newSuspendedTransaction(db = database) {
        update(where = { ID eq id.toJavaUuid() }) {
            it[STATUS] = status
        }
    }

    suspend fun delete(uuid: Uuid) = newSuspendedTransaction(db = database) {
        deleteWhere {
            it.run {
                ID eq uuid.toJavaUuid()
            }
        }
    }

    suspend fun deleteByTelegramId(tgId: Long) = newSuspendedTransaction(db = database) {
        deleteWhere {
            it.run {
                TELEGRAM_ID eq tgId
            }
        }
    }

    suspend fun hasAnyFromTgId(id: Long) = newSuspendedTransaction(db = database) {
        !selectAll().where { TELEGRAM_ID eq id }.empty()
    }

    @TestOnly
    suspend fun clear(): Unit = newSuspendedTransaction(db = database) {
        deleteAll()
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
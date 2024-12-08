package com.y9vad9.bcm.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class UsersTable(
    private val database: Database,
) {
    companion object : Table(name = "users") {
        val ID = uuid("id")
        val TELEGRAM_ID = long("tg_id").uniqueIndex()
        val DISPLAY_NAME = text("display_name").nullable()
        val CREATION_TIME = long("creation_time")
    }

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun createIfNotExists(
        id: Uuid,
        tgId: Long,
        displayName: String?,
        creationTime: Long,
    ): Unit = newSuspendedTransaction(db = database) {
        insertIgnore {
            it[ID] = id.toJavaUuid()
            it[TELEGRAM_ID] = tgId
            it[DISPLAY_NAME] = displayName
            it[CREATION_TIME] = creationTime
        }
    }

    suspend fun update(
        id: Uuid,
        newTelegramId: Long? = null,
        newDisplayName: String? = null,
    ): Unit = newSuspendedTransaction(db = database) {
        update(where = { ID eq id.toJavaUuid() }) {
            if (newTelegramId != null)
                it[TELEGRAM_ID] = newTelegramId
            if (newDisplayName != null)
                it[DISPLAY_NAME] = newDisplayName
        }
    }

    suspend fun get(tgId: Long): Entity? = newSuspendedTransaction(db = database) {
        select { TELEGRAM_ID eq tgId }
            .firstOrNull()?.toEntity()
    }

    suspend fun get(uuid: Uuid): Entity? = newSuspendedTransaction(db = database) {
        select { ID eq uuid.toJavaUuid() }
            .firstOrNull()?.toEntity()
    }

    data class Entity(
        val id: Uuid,
        val telegramId: Long,
        val displayName: String?,
        val creationTime: Long,
    )


    private fun ResultRow.toEntity(): UsersTable.Entity {
        return UsersTable.Entity(
            id = get(ID).toKotlinUuid(),
            telegramId = get(TELEGRAM_ID),
            displayName = get(DISPLAY_NAME),
            creationTime = get(CREATION_TIME),
        )
    }
}

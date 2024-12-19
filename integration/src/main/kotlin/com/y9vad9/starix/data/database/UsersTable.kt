package com.y9vad9.starix.data.database

import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
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
        val LANGUAGE_CODE = varchar("language_code", 2).nullable()
        val ZONE_ID = varchar("zone_id", 64).nullable()
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
        selectAll().where { TELEGRAM_ID eq tgId }
            .firstOrNull()?.toEntity()
    }

    suspend fun get(uuid: Uuid): Entity? = newSuspendedTransaction(db = database) {
        selectAll().where { ID eq uuid.toJavaUuid() }
            .firstOrNull()?.toEntity()
    }

    data class Entity(
        val id: Uuid,
        val telegramId: Long,
        val displayName: String?,
        val languageCode: String?,
        val zoneId: String?,
        val creationTime: Long,
    )

    @TestOnly
    suspend fun clear(): Unit = newSuspendedTransaction(db = database) {
        deleteAll()
    }


    private fun ResultRow.toEntity(): Entity {
        return Entity(
            id = get(ID).toKotlinUuid(),
            telegramId = get(TELEGRAM_ID),
            displayName = get(DISPLAY_NAME),
            languageCode = get(LANGUAGE_CODE),
            zoneId = get(ZONE_ID),
            creationTime = get(CREATION_TIME),
        )
    }
}

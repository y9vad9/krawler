package com.y9vad9.bcm.data.database

import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.entity.brawlstars.value.PlayerTag
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class UserBSAccountsTable(
    private val database: Database,
) {
    companion object : Table(name = "user_bs_accounts") {
        val USER_ID = uuid("user_id").references(UsersTable.ID)
        val PLAYER_TAG = varchar("player_tag", PlayerTag.REQUIRED_SIZE)
        val CREATION_TIME = long("creation_time")
    }

    init {
        transaction(database) {
            SchemaUtils.create(UserBSAccountsTable)
        }
    }

    suspend fun create(
        userId: Uuid,
        playerTag: String,
        creationTime: Long,
    ): Unit = newSuspendedTransaction(db = database) {
        insert {
            it[USER_ID] = userId.toJavaUuid()
            it[PLAYER_TAG] = playerTag
            it[CREATION_TIME] = creationTime
        }
    }

    suspend fun getListOfPlayerTags(
        userId: Uuid,
    ): List<String> = newSuspendedTransaction(db = database) {
        select { USER_ID eq userId.toJavaUuid() }.toList().map { it[PLAYER_TAG] }
    }

    suspend fun getPlayerSystemUuid(
        tag: String,
    ): Uuid? = newSuspendedTransaction(db = database) {
        select { PLAYER_TAG eq tag }.firstOrNull()?.get(USER_ID)?.toKotlinUuid()
    }

    suspend fun removeLinkageIfExists(playerTag: String): Unit = newSuspendedTransaction(db = database) {
        deleteWhere { PLAYER_TAG eq playerTag }
    }

    suspend fun remove(
        userId: Uuid,
        playerTag: String,
    ): Unit = newSuspendedTransaction(db = database) {
        deleteWhere { (USER_ID eq userId.toJavaUuid()) and (PLAYER_TAG eq playerTag) }
    }
}
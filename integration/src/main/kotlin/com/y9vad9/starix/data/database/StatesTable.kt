package com.y9vad9.starix.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class StatesTable(private val database: Database) {
    companion object : Table("bot_states") {
        val CHAT_ID = long("chat_id")
        val STATE_JSON = text("state_json")
        val LAST_UPDATE_TIME = long("last_update_time")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Companion)
        }
    }

    suspend fun createOrUpdate(
        chatId: Long,
        stateJson: String,
        time: Long,
    ): Unit = newSuspendedTransaction(db = database) {
        insert {
            it[CHAT_ID] = chatId
            it[STATE_JSON] = stateJson
            it[LAST_UPDATE_TIME] = time
        }
    }

    suspend fun exists(chatId: Long): Boolean = newSuspendedTransaction(db = database) {
        !selectAll().where { CHAT_ID eq chatId }.empty()
    }

    suspend fun getJson(chatId: Long): String? = newSuspendedTransaction(db = database) {
        selectAll().where { CHAT_ID eq chatId }.limit(1).singleOrNull()?.get(STATE_JSON)
    }

    suspend fun remove(chatId: Long): Unit = newSuspendedTransaction(db = database) {
        deleteWhere { CHAT_ID eq chatId }
    }
}
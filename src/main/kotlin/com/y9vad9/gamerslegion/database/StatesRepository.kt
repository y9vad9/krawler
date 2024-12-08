package com.y9vad9.gamerslegion.database

import com.y9vad9.gamerslegion.database.table.StatesTable
import com.y9vad9.gamerslegion.database.table.StatesTable.JSON
import com.y9vad9.gamerslegion.database.table.StatesTable.USER_ID
import com.y9vad9.gamerslegion.telegram.state.FSMState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class StatesRepository(private val database: Database, private val json: Json) {
    suspend fun setState(chatId: Long, state: FSMState): Unit =
        newSuspendedTransaction(db = database) {
            StatesTable.insert { statement ->
                statement[USER_ID] = chatId
                statement[JSON] = json.encodeToString(state)
            }
        }

    suspend fun getState(chatId: Long): FSMState? = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction StatesTable.select {
            USER_ID eq chatId
        }.firstOrNull()?.get(JSON)?.let { json.decodeFromString<FSMState>(it) }
    }
}
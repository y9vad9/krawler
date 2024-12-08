package com.y9vad9.gamerslegion.database.table

import org.jetbrains.exposed.sql.Table

object StatesTable : Table() {
    val USER_ID = long("user_id")
    val JSON = text("json")
}
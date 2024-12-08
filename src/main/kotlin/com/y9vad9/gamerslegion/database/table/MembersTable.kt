package com.y9vad9.gamerslegion.database.table

import org.jetbrains.exposed.sql.Table

object MembersTable : Table("members_history") {
    val BS_TAG = text("bs_tag").uniqueIndex()
    val BS_NAME = text("bs_name")
    val TG_ID = long("tg_id")
    val IS_EXCUSED = bool("is_excused").default(false)
    val IS_ADMIN = bool("is_admin").default(false)
}
package com.y9vad9.gamerslegion.database

import com.y9vad9.gamerslegion.brawlstars.entity.Member
import com.y9vad9.gamerslegion.database.table.MembersTable
import com.y9vad9.gamerslegion.database.table.MembersTable.TG_ID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class MembersRepository(
    private val database: Database,
) {

    init {
        transaction(database) {
            SchemaUtils.create(MembersTable)
        }
    }

    suspend fun getByTag(tag: String): MemberInfo? = newSuspendedTransaction(db = database) {
        MembersTable.select { MembersTable.BS_TAG eq tag }.firstOrNull()?.toMemberInfo()
    }

    suspend fun getByTgId(id: Long): MemberInfo? = newSuspendedTransaction(db = database) {
        MembersTable.select { TG_ID eq id }.firstOrNull()?.toMemberInfo()
    }

    suspend fun addMember(
        tgId: Long,
        name: String,
        bsTag: String,
    ): Unit = newSuspendedTransaction(db = database) {
        MembersTable.insert { builder ->
            builder[TG_ID] = tgId
            builder[BS_TAG] = bsTag
            builder[BS_NAME] = name
        }
    }
}

data class MemberInfo(
    val tgId: Long,
    val tag: String,
    val name: String,
    val isExcused: Boolean,
    val isAdmin: Boolean,
)

private fun ResultRow.toMemberInfo(): MemberInfo {
    return MemberInfo(
        tgId = this[MembersTable.TG_ID],
        tag = this[MembersTable.BS_TAG],
        name = this[MembersTable.BS_NAME],
        isExcused = this[MembersTable.IS_EXCUSED],
        isAdmin = this[MembersTable.IS_ADMIN],
    )
}
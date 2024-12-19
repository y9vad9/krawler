package com.y9vad9.starix.data.test.database

import com.y9vad9.starix.data.database.UserBSAccountsTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class UserBSAccountsTableTest {
    private val db = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;MODE=MYSQL;")
    private val table = UserBSAccountsTable(db)

    @BeforeTest
    fun `clear table`(): Unit = transaction(db) {
        SchemaUtils.drop(UserBSAccountsTable)
        SchemaUtils.create(UserBSAccountsTable)
    }

    @Test
    fun `create and retrieve player tags`(): Unit = runTest {
        val userId = Uuid.random()
        val playerTag1 = "#TAG1234"
        val playerTag2 = "#TAG5678"
        val creationTime = System.currentTimeMillis()

        table.create(userId, playerTag1, creationTime)
        table.create(userId, playerTag2, creationTime + 1000)

        val retrievedTags = table.getListOfPlayerTags(userId)
        assertEquals(listOf(playerTag1, playerTag2), retrievedTags)
    }

    @Test
    fun `retrieve system UUID by player tag`(): Unit = runTest {
        val userId = Uuid.random()
        val playerTag = "#TAG1234"
        val creationTime = System.currentTimeMillis()

        table.create(userId, playerTag, creationTime)

        val retrievedUuid = table.getPlayerByTag(playerTag)
        assertNotNull(retrievedUuid)
        assertEquals(userId, retrievedUuid)
    }

    @Test
    fun `remove linkage if exists`(): Unit = runTest {
        val userId = Uuid.random()
        val playerTag = "#TAG1234"
        val creationTime = System.currentTimeMillis()

        table.create(userId, playerTag, creationTime)

        table.removeLinkageIfExists(playerTag)

        val retrievedUuid = table.getPlayerByTag(playerTag)
        assertNull(retrievedUuid)
    }

    @Test
    fun `remove specific player tag for a user`(): Unit = runTest {
        val userId = Uuid.random()
        val playerTag1 = "#TAG1234"
        val playerTag2 = "#TAG5678"
        val creationTime = System.currentTimeMillis()

        table.create(userId, playerTag1, creationTime)
        table.create(userId, playerTag2, creationTime + 1000)

        table.remove(userId, playerTag1)

        val remainingTags = table.getListOfPlayerTags(userId)
        assertEquals(listOf(playerTag2), remainingTags)
    }
}

package com.y9vad9.starix.data.test.database

import com.y9vad9.bcm.data.database.UsersTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class UsersTableTest {
    private val db = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;MODE=MYSQL;")
    private val table = UsersTable(db)

    @BeforeTest
    fun `clear table`(): Unit = runTest {
        table.clear()
    }

    @Test
    fun `create a new user`(): Unit = runTest {
        table.createIfNotExists(
            id = Uuid.random(),
            tgId = Random.nextLong(0, Long.MAX_VALUE),
            displayName = "Test User",
            creationTime = System.currentTimeMillis(),
        )
    }

    @Test
    fun `create a new user that exists`(): Unit = runTest {
        val tgId = Random.nextLong(0, Long.MAX_VALUE)

        repeat(2) {
            table.createIfNotExists(
                id = Uuid.random(),
                tgId = tgId,
                displayName = "Test User",
                creationTime = System.currentTimeMillis(),
            )
        }

        assertNotNull(table.get(tgId))
    }

    @Test
    fun `get user by uuid`(): Unit = runTest {
        val tgId = Random.nextLong(0, Long.MAX_VALUE)
        val uuid = Uuid.random()

        table.createIfNotExists(
            id = uuid,
            tgId = tgId,
            displayName = "Test User",
            creationTime = System.currentTimeMillis(),
        )

        assertNotNull(table.get(tgId))
    }

    @Test
    fun `get user by tg id`(): Unit = runTest {
        val uuid = Uuid.random()

        table.createIfNotExists(
            id = uuid,
            tgId = Random.nextLong(0, Long.MAX_VALUE),
            displayName = "Test User",
            creationTime = System.currentTimeMillis(),
        )

        assertNotNull(table.get(uuid))
    }

    @Test
    fun `update user successfully`(): Unit = runTest {
        val uuid = Uuid.random()

        table.createIfNotExists(
            id = uuid,
            tgId = Random.nextLong(2, Long.MAX_VALUE),
            displayName = "Test User",
            creationTime = System.currentTimeMillis(),
        )

        table.update(uuid, newTelegramId = 1)

        val user = table.get(uuid)
        assertNotNull(user)
        assertEquals(
            expected = 1,
            actual = user.telegramId,
        )
    }
}
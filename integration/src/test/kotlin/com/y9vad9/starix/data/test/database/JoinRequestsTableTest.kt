package com.y9vad9.starix.data.test.database

import com.y9vad9.starix.data.database.JoinRequestsTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import kotlin.test.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class JoinRequestsTableTest {
    private val db = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;MODE=MYSQL;")
    private val table = JoinRequestsTable(db)

    @BeforeTest
    fun `clear table`() = runTest {
        table.clear()
    }

    @Test
    fun `create join request`() = runTest {
        val joinRequest = table.create(
            id = Uuid.random(),
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = 12345L,
            message = "Test message"
        )
        assertNotNull(joinRequest)
    }

    @Test
    fun `get join request by id`() = runTest {
        val id = Uuid.random()
        table.create(
            id = id,
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = 54321L,
            message = "Another test message"
        )
        val joinRequest = table.get(id)
        assertNotNull(joinRequest)
        assertEquals("#PLAYER123", joinRequest?.playerTag?.toString())
    }

    @Test
    fun `count join requests by status`() = runTest {
        table.create(
            id = Uuid.random(),
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = 12345L,
            message = "Message for counting",
        )
        val count = table.count(JoinRequestsTable.Status.UNDECIDED)
        assertEquals(1, count)
    }

    @Test
    fun `get list of join requests by status`() = runTest {
        repeat(3) {
            table.create(
                id = Uuid.random(),
                playerTag = "#PLAYER1_$it",
                clubTag = "#CLUB123_$it",
                tgId = 1000L + it,
                message = "List message $it",
            )
        }
        val joinRequests = table.getList(JoinRequestsTable.Status.UNDECIDED, size = 2)
        assertEquals(2, joinRequests.size)
    }

    @Test
    fun `update join request status`() = runTest {
        val id = Uuid.random()
        table.create(
            id = id,
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = 67890L,
            message = "Update test message"
        )
        table.updateStatus(id, JoinRequestsTable.Status.ACCEPTED)
        val count = table.count(JoinRequestsTable.Status.ACCEPTED)
        assertEquals(1, count)
    }

    @Test
    fun `delete join request by id`() = runTest {
        val id = Uuid.random()
        table.create(
            id = id,
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = 13579L,
            message = "Delete test message"
        )
        table.delete(id)
        val joinRequest = table.get(id)
        assertNull(joinRequest)
    }

    @Test
    fun `delete join request by telegram id`() = runTest {
        val tgId = 24680L
        table.create(
            id = Uuid.random(),
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = tgId,
            message = "Delete by tgId"
        )
        table.deleteByTelegramId(tgId)
        val hasAny = table.hasAnyFromTgId(tgId)
        assertFalse(hasAny)
    }

    @Test
    fun `check if join request exists by telegram id`() = runTest {
        val tgId = 112233L
        table.create(
            id = Uuid.random(),
            playerTag = "#PLAYER123",
            clubTag = "#CLUB12345",
            tgId = tgId,
            message = "Exists test"
        )
        val exists = table.hasAnyFromTgId(tgId)
        assertTrue(exists)
    }
}

package com.y9vad9.starix.data.test.database

import com.y9vad9.bcm.data.database.BSClubHistoryTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BSClubHistoryTableTest {
    private val db = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;MODE=MYSQL;")
    private val table = BSClubHistoryTable(db)

    @BeforeTest
    fun `clear table`(): Unit = runTest {
        transaction(db) {
            SchemaUtils.drop(BSClubHistoryTable)
            SchemaUtils.create(BSClubHistoryTable)
        }
    }

    @Test
    fun `create and retrieve last JSON before unix time`(): Unit = runTest {
        val clubTag = "#12345"
        val unixTime1 = System.currentTimeMillis() - 10000
        val unixTime2 = System.currentTimeMillis()
        val json1 = "{\"tag\": \"#12345\", \"name\": \"Club1\"}"
        val json2 = "{\"tag\": \"#12345\", \"name\": \"Club2\"}"

        table.create(clubTag, json1, unixTime1)
        table.create(clubTag, json2, unixTime2)

        val retrievedJson = table.getJsonOfLastBefore(clubTag, unixTime2 - 5000)
        assertNotNull(retrievedJson)
        assertEquals(json1, retrievedJson)
    }

    @Test
    fun `create and retrieve first JSON after unix time`(): Unit = runTest {
        val clubTag = "#12345"
        val unixTime1 = System.currentTimeMillis() - 20000
        val unixTime2 = System.currentTimeMillis() - 10000
        val json1 = "{\"tag\": \"#12345\", \"name\": \"Club1\"}"
        val json2 = "{\"tag\": \"#12345\", \"name\": \"Club2\"}"

        table.create(clubTag, json1, unixTime1)
        table.create(clubTag, json2, unixTime2)

        val retrievedJson = table.getJsonOfFirstAfter(clubTag, unixTime1 + 5000)
        assertNotNull(retrievedJson)
        assertEquals(json2, retrievedJson)
    }

    @Test
    fun `retrieve JSON when no matching time exists`(): Unit = runTest {
        val clubTag = "#12345"
        val unixTime = System.currentTimeMillis() - 10000
        val json = "{\"tag\": \"#12345\", \"name\": \"Club1\"}"

        table.create(clubTag, json, unixTime)

        val retrievedJson = table.getJsonOfLastBefore(clubTag, unixTime - 5000)
        assertEquals(null, retrievedJson)

        val retrievedJsonAfter = table.getJsonOfFirstAfter(clubTag, unixTime + 5000)
        assertEquals(null, retrievedJsonAfter)
    }
}

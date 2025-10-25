package krawler.server.brawlstars.client.test.serialization

import krawler.server.brawlstars.client.event.RawScheduledEvent
import krawler.server.brawlstars.client.test.JsonFixturesTest
import kotlin.test.Test
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.assertDoesNotThrow

class EventJsonSerializationTest : JsonFixturesTest() {
    @Test
    fun `check no errors when serializing test fixtures battlelog`() {
        // GIVEN
        val filePath = "/api/v1/events/rotation/example_response.json"
        val fileStream = loadFixtureAsStream(filePath)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $filePath into PaginatedList<BattleRecord>"
        ) {
            json.decodeFromStream<List<RawScheduledEvent>>(fileStream)
        }
    }
}

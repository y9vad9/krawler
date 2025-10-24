@file:OptIn(ExperimentalSerializationApi::class)

package krawler.server.brawlstars.client.test.serialization

import krawler.server.brawlstars.client.battle.RawBattleRecord
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.test.JsonFixturesTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class BattleJsonSerializationTest : JsonFixturesTest() {
    @Test
    fun `check no errors when serializing test fixtures battlelog`() {
        // GIVEN
        val basePath = "/api/v1/players/battlelog"
        val files = listOf(
            "$basePath/example_response_1.json",
            "$basePath/example_response_2.json",
            "$basePath/example_response_3.json",
        )

        // WHEN & THEN
        files.forEach { filePath ->
            assertDoesNotThrow(
                message = "Couldn't serialize $filePath into PaginatedList<BattleRecord>"
            ) {
                json.decodeFromStream<RawPaginatedList<RawBattleRecord>>(
                    stream = loadFixtureAsStream(filePath)
                )
            }
        }
    }
}

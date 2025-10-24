package krawler.server.brawlstars.client.test.serialization

import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.ranking.RawClubRanking
import krawler.server.brawlstars.client.ranking.RawPlayerRanking
import krawler.server.brawlstars.client.test.JsonFixturesTest
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class RankingsJsonSerializationTest : JsonFixturesTest() {
    @Test
    fun `check serialization of non-paginated top-100 players of a brawler does not throw`() {
        // GIVEN
        val path = "api/v1/rankings/brawlers/no_paging.json"
        val fileStream = loadFixtureAsStream(path)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $path into PaginatedList<PlayerRanking>"
        ) {
            json.decodeFromStream<RawPaginatedList<RawPlayerRanking>>(fileStream)
        }
    }

    @Test
    fun `check serialization of paginated top-100 players of a brawler does not throw`() {
        // GIVEN
        val basePath = "api/v1/rankings/brawlers"
        val files = listOf(
            "$basePath/paging_1.json",
            "$basePath/paging_2.json",
            "$basePath/paging_3.json",
        )

        // WHEN & THEN
        files.forEach { filePath ->
            assertDoesNotThrow(
                message = "Couldn't serialize $filePath into PaginatedList<PlayerRanking>"
            ) {
                json.decodeFromStream<RawPaginatedList<RawPlayerRanking>>(
                    stream = loadFixtureAsStream(filePath)
                )
            }
        }
    }

    @Test
    fun `check serialization of non-paginated top-100 players does not throw`() {
        // GIVEN
        val path = "api/v1/rankings/players/no_paging.json"
        val fileStream = loadFixtureAsStream(path)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $path into PaginatedList<PlayerRanking>"
        ) {
            json.decodeFromStream<RawPaginatedList<RawPlayerRanking>>(fileStream)
        }
    }

    @Test
    fun `check serialization of paginated top-100 players does not throw`() {
        // GIVEN
        val basePath = "api/v1/rankings/brawlers"
        val files = listOf(
            "$basePath/paging_1.json",
            "$basePath/paging_2.json",
            "$basePath/paging_3.json",
        )

        // WHEN & THEN
        files.forEach { filePath ->
            assertDoesNotThrow(
                message = "Couldn't serialize $filePath into PaginatedList<PlayerRanking>"
            ) {
                json.decodeFromStream<RawPaginatedList<RawPlayerRanking>>(
                    stream = loadFixtureAsStream(filePath)
                )
            }
        }
    }

    @Test
    fun `check serialization of non-paginated top-100 clubs does not throw`() {
        // GIVEN
        val path = "api/v1/rankings/clubs/no_paging.json"
        val fileStream = loadFixtureAsStream(path)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $path into PaginatedList<ClubRanking>"
        ) {
            json.decodeFromStream<RawPaginatedList<RawClubRanking>>(fileStream)
        }
    }

    @Test
    fun `check serialization of paginated top-100 clubs does not throw`() {
        // GIVEN
        val basePath = "api/v1/rankings/clubs"
        val files = listOf(
            "$basePath/paging_1.json",
            "$basePath/paging_2.json",
            "$basePath/paging_3.json",
        )

        // WHEN & THEN
        files.forEach { filePath ->
            assertDoesNotThrow(
                message = "Couldn't serialize $filePath into PaginatedList<ClubRanking>"
            ) {
                json.decodeFromStream<RawPaginatedList<RawClubRanking>>(
                    stream = loadFixtureAsStream(filePath)
                )
            }
        }
    }
}

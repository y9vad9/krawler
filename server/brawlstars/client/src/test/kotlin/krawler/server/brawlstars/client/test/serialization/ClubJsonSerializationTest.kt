package krawler.server.brawlstars.client.test.serialization

import krawler.server.brawlstars.client.club.RawClub
import krawler.server.brawlstars.client.club.RawClubMember
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.test.JsonFixturesTest
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ClubJsonSerializationTest : JsonFixturesTest() {
    @Test
    fun `check no errors when serializing fixture club returned from API`() {
        // GIVEN
        val path = "api/v1/clubs/example_response.json"
        val fileStream = loadFixtureAsStream(path)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $path into Club"
        ) {
            json.decodeFromStream<RawClub>(fileStream)
        }
    }

    @Test
    fun `check no errors when serializing fixture non-paginated list of club members list returned from API`() {
        // GIVEN
        val path = "api/v1/clubs/members/no_paging.json"
        val fileStream = loadFixtureAsStream(path)

        // WHEN & THEN
        assertDoesNotThrow(
            message = "Couldn't serialize $path into ClubMember"
        ) {
            json.decodeFromStream<RawPaginatedList<RawClubMember>>(fileStream)
        }
    }

    @Test
    fun `check no errors when serializing fixture paginated club members list returned from API`() {
        // GIVEN
        val basePath = "api/v1/clubs/members"
        val files = listOf(
            "$basePath/paging_1.json",
            "$basePath/paging_2.json",
            "$basePath/paging_3.json",
        )

        // WHEN & THEN
        files.forEach { filePath ->
            assertDoesNotThrow(
                message = "Couldn't serialize $filePath into PaginatedList<ClubMember>"
            ) {
                json.decodeFromStream<RawPaginatedList<RawClubMember>>(
                    stream = loadFixtureAsStream(filePath)
                )
            }
        }
    }
}

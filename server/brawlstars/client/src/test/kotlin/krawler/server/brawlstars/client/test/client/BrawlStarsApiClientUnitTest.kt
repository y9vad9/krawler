package krawler.server.brawlstars.client.test.client

import krawler.server.brawlstars.client.BrawlStarsApiClient
import krawler.server.brawlstars.client.BrawlStarsApiException
import krawler.server.brawlstars.client.RawClientError
import krawler.server.brawlstars.client.InternalServerErrorException
import krawler.server.brawlstars.client.LimitsExceededException
import krawler.server.brawlstars.client.UnderMaintenanceException
import krawler.server.brawlstars.client.battle.RawBattleRecord
import krawler.server.brawlstars.client.club.RawClub
import krawler.server.brawlstars.client.club.RawClubMember
import krawler.server.brawlstars.client.event.RawScheduledEvent
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.player.RawPlayer
import krawler.server.brawlstars.client.ranking.RawClubRanking
import krawler.server.brawlstars.client.ranking.RawPlayerRanking
import krawler.server.brawlstars.client.rateLimited
import krawler.server.brawlstars.client.test.JsonFixturesTest
import krawler.server.brawlstars.client.tryGetBrawlerRanking
import krawler.server.brawlstars.client.tryGetClub
import krawler.server.brawlstars.client.tryGetClubMembers
import krawler.server.brawlstars.client.tryGetClubRanking
import krawler.server.brawlstars.client.tryGetEventsRotation
import krawler.server.brawlstars.client.tryGetPlayer
import krawler.server.brawlstars.client.tryGetPlayerBattleLog
import krawler.server.brawlstars.client.tryGetPlayerRanking
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.assertDoesNotThrow

class BrawlStarsApiClientUnitTest : JsonFixturesTest() {

    @Test
    fun `getPlayer should not throw`() = runTest {
        runDoesNotThrow("api/v1/players/example_response.json") { client ->
            client.getPlayer("#TAG")
        }
    }

    @Test
    fun `getPlayer should not throw and return null if 404 is responded`() = runTest {
        // GIVEN
        val clients = createTestClients(
            MockEngine {
                respond(
                    """{ "reason": "notFound" }""".trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    status = HttpStatusCode.NotFound,
                )
            }
        )

        // WHEN
        val results = clients.map { (name, client) ->
            name to client.getPlayer("#TAG")
        }

        // THEN
        results.forEach { (name, result) ->
            assertDoesNotThrow(
                message = "Not found status shouldn't return exceptions," +
                    " but $name client returns null",
            ) {
                result.getOrThrow()
            }

            assertNull(
                actual = result.getOrThrow(),
                message = "404 status code should make result contain null",
            )
        }
    }

    @Test
    fun `getPlayer should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getPlayer("#ANY") }
    }

    @Test
    fun `getPlayerBattleLog should not throw`() = runTest {
        runDoesNotThrow("api/v1/players/battlelog/example_response_1.json") { client ->
            client.getPlayerBattleLog("#TAG")
        }
    }

    @Test
    fun `getClub should not throw and return null if 404 is responded`() = runTest {
        // GIVEN
        val clients = createTestClients(
            MockEngine {
                respond(
                    """{ "reason": "notFound" }""".trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    status = HttpStatusCode.NotFound,
                )
            }
        )

        // WHEN
        val results = clients.map { (name, client) ->
            name to client.getClub("#TAG")
        }

        // THEN
        results.forEach { (name, result) ->
            assertDoesNotThrow(
                message = "Not found status shouldn't return exceptions," +
                    " but $name client returns null",
            ) {
                result.getOrThrow()
            }

            assertNull(
                actual = result.getOrThrow(),
                message = "404 status code should make result contain null",
            )
        }
    }

    @Test
    fun `getClub should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getClub("#ANY") }
    }

    @Test
    fun `getClub should not throw`() = runTest {
        runDoesNotThrow("api/v1/clubs/example_response.json") { client ->
            client.getClub("#TAG")
        }
    }

    @Test
    fun `getClubMembers should not throw`() = runTest {
        runDoesNotThrow("api/v1/clubs/members/no_paging.json") { client ->
            client.getClubMembers("#TAG")
        }
    }

    @Test
    fun `getClubMembers should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getClubMembers("#ANY") }
    }

    @Test
    fun `getClubMembers should not throw and return null if 404 is responded`() = runTest {
        // GIVEN
        val clients = createTestClients(
            MockEngine {
                respond(
                    """{ "reason": "notFound" }""".trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    status = HttpStatusCode.NotFound,
                )
            }
        )

        // WHEN
        val results = clients.map { (name, client) ->
            name to client.getClubMembers("#TAG")
        }

        // THEN
        results.forEach { (name, result) ->
            assertDoesNotThrow(
                message = "Not found status shouldn't return exceptions," +
                    " but $name client returns null",
            ) {
                result.getOrThrow()
            }

            assertNull(
                actual = result.getOrThrow(),
                message = "404 status code should make result contain null",
            )
        }
    }

    @Test
    fun `getBrawlerRanking should not throw`() = runTest {
        runDoesNotThrow("api/v1/rankings/brawlers/no_paging.json") { client ->
            client.getBrawlerRanking(Int.MAX_VALUE)
        }
    }

    @Test
    fun `getBrawlerRanking should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getBrawlerRanking(Int.MAX_VALUE) }
    }

    @Test
    fun `getPlayerRanking should not throw`() = runTest {
        runDoesNotThrow("api/v1/rankings/players/no_paging.json") { client ->
            client.getBrawlerRanking(Int.MAX_VALUE)
        }
    }

    @Test
    fun `getPlayerRanking should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getPlayerRanking() }
    }

    @Test
    fun `getClubRanking should not throw`() = runTest {
        runDoesNotThrow("api/v1/rankings/clubs/no_paging.json") { client ->
            client.getClubRanking()
        }
    }

    @Test
    fun `getClubRanking should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getClubRanking() }
    }

    @Test
    fun `getEventRotation should not throw`() = runTest {
        runDoesNotThrow("api/v1/events/rotation/example_response.json") { client ->
            client.getEventsRotation()
        }
    }

    @Test
    fun `getEventRotation should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getEventsRotation() }
    }

    @Test
    fun `getPlayerBattlelog should not throw and return null if 404 is responded`() = runTest {
        // GIVEN
        val clients = createTestClients(
            MockEngine {
                respond(
                    """{ "reason": "notFound" }""".trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    status = HttpStatusCode.NotFound,
                )
            }
        )

        // WHEN
        val results = clients.map { (name, client) ->
            name to client.getPlayerBattleLog("#TAG")
        }

        // THEN
        results.forEach { (name, result) ->
            assertDoesNotThrow(
                message = "Not found status shouldn't return exceptions," +
                    " but $name client returns null",
            ) {
                result.getOrThrow()
            }

            assertNull(
                actual = result.getOrThrow(),
                message = "404 status code should make result contain null",
            )
        }
    }

    @Test
    fun `getPlayerBattlelog should return correct exception for each http code`() = runTest {
        runErrorMappingTest { client -> client.getPlayerBattleLog("#ANY") }
    }


    @Test
    fun `tryGetPlayer should return player when getPlayer succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = mockk<RawPlayer>()
        coEvery { client.getPlayer("#TAG") } returns Result.success(expected)

        // WHEN
        val result = client.tryGetPlayer(tag = "#TAG")

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetPlayer should return null when getPlayer fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getPlayer("#TAG") } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetPlayer(tag = "#TAG")

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetPlayerBattleLog should return list when getPlayerBattleLog succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawBattleRecord>())
        coEvery { client.getPlayerBattleLog("#TAG") } returns Result.success(expected)

        // WHEN
        val result = client.tryGetPlayerBattleLog(tag = "#TAG")

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetPlayerBattleLog should return null when getPlayerBattleLog fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getPlayerBattleLog("#TAG") } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetPlayerBattleLog(tag = "#TAG")

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetClub should return club when getClub succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = mockk<RawClub>()
        coEvery { client.getClub("#TAG") } returns Result.success(expected)

        // WHEN
        val result = client.tryGetClub(tag = "#TAG")

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetClub should return null when getClub fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getClub("#TAG") } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetClub(tag = "#TAG")

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetClubMembers should return list when getClubMembers succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawClubMember>())
        coEvery { client.getClubMembers("#TAG") } returns Result.success(expected)

        // WHEN
        val result = client.tryGetClubMembers(tag = "#TAG")

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetClubMembers should return null when getClubMembers fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getClubMembers("#TAG") } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetClubMembers(tag = "#TAG")

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetBrawlerRanking should return list when getBrawlerRanking succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawPlayerRanking>())
        coEvery { client.getBrawlerRanking(brawlerId = 1, ) } returns Result.success(RawPaginatedList(items = expected))

        // WHEN
        val result = client.tryGetBrawlerRanking(brawlerId = 1, )

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetBrawlerRanking should return null when getBrawlerRanking fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getBrawlerRanking(brawlerId = 1, ) } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetBrawlerRanking(brawlerId = 1, )

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetPlayerRanking should return list when getPlayerRanking succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawPlayerRanking>())
        coEvery { client.getPlayerRanking() } returns Result.success(RawPaginatedList(items = expected))

        // WHEN
        val result = client.tryGetPlayerRanking()

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetPlayerRanking should return null when getPlayerRanking fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getPlayerRanking() } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetPlayerRanking()

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetClubRanking should return list when getClubRanking succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawClubRanking>())
        coEvery { client.getClubRanking() } returns Result.success(
            RawPaginatedList(
                items = expected,
            )
        )

        // WHEN
        val result = client.tryGetClubRanking()

        // THEN
        assertNotNull(result)
        assertEquals(
            expected = expected,
            actual = result,
        )
    }

    @Test
    fun `tryGetClubRanking should return null when getClubRanking fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getClubRanking() } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetClubRanking()

        // THEN
        assertNull(result)
    }

    @Test
    fun `tryGetEventsRotation should return list when getEventsRotation succeeds`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        val expected = listOf(mockk<RawScheduledEvent>())
        coEvery { client.getEventsRotation() } returns Result.success(expected)

        // WHEN
        val result = client.tryGetEventsRotation()

        // THEN
        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `tryGetEventsRotation should return null when getEventsRotation fails`() = runTest {
        // GIVEN
        val client = mockk<BrawlStarsApiClient>()
        coEvery { client.getEventsRotation() } returns Result.failure(RuntimeException())

        // WHEN
        val result = client.tryGetEventsRotation()

        // THEN
        assertNull(result)
    }

    private suspend fun runErrorMappingTest(call: suspend (BrawlStarsApiClient) -> Result<*>) {
        // GIVEN
        val errorsWithCodesAndExpectedException = listOf(
            Triple(
                RawClientError(
                    reason = "accessDenied",
                    message = "Invalid authorization"
                ),
                HttpStatusCode.Forbidden,
                AccessDeniedException::class
            ),
            Triple(
                RawClientError(
                    reason = "tooManyRequests",
                    details = buildJsonObject {
                        put("foo", "bar")
                    }
                ),
                HttpStatusCode.TooManyRequests,
                LimitsExceededException::class
            ),
            Triple(
                RawClientError(
                    reason = "unknownError",
                    type = "unknown"
                ),
                HttpStatusCode.InternalServerError,
                InternalServerErrorException::class
            ),
            Triple(
                RawClientError(
                    reason = "under"
                ),
                HttpStatusCode.ServiceUnavailable,
                UnderMaintenanceException::class
            )
        )

        val engines = errorsWithCodesAndExpectedException.map { (error, code, expectedException) ->
            val engine = MockEngine {
                respond(
                    content = json.encodeToString(error),
                    status = code,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

            Triple(code, engine, expectedException)
        }

        // WHEN & THEN
        engines.forEach { (code, engine, expectedException) ->
            createTestClients(engine).forEach { (clientName, client) ->
                val result = call(client)

                val exception = result.exceptionOrNull()

                assertNotNull(
                    actual = exception,
                    message = "Expected $clientName client to return an exception.",
                )
                assert(value = exception::class == expectedException) {
                    "Expected $clientName client to return ${expectedException.simpleName}" +
                        " for a ${code.value} http code."
                }
                assertEquals(
                    expected = errorsWithCodesAndExpectedException
                        .first { it.second == code }
                        .first,
                    actual = (exception as BrawlStarsApiException).error,
                )
            }
        }
    }

    private suspend fun runDoesNotThrow(
        fixturePath: String,
        call: suspend (BrawlStarsApiClient) -> Result<*>,
    ) {
        // GIVEN
        val clients = createTestClients(
            MockEngine {
                respond(
                    loadFixtureAsStream(fixturePath).toByteReadChannel(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        )

        // WHEN
        val results = clients.map { (name, client) ->
            name to assertDoesNotThrow("$name client failed") {
                call(client).getOrThrow()
            }
        }

        // THEN
        results.forEachIndexed { index, (name, club) ->
            assertNotNull(club, "$name client returned null")

            if (index > 0) {
                assertEquals(
                    expected = results[0].second,
                    actual = club,
                    message = "$name client returned inconsistent result",
                )
            }
        }
    }

    private fun createTestClients(engine: MockEngine): List<Pair<String, BrawlStarsApiClient>> {
        val defaultClient = BrawlStarsApiClient.create(
            engine = engine,
            bearerToken = "",
            json = json,
        )
        return listOf(
            "Default" to defaultClient,
            "Proxy" to BrawlStarsApiClient.createWithRoyaleApiProxy(
                engine = engine,
                bearerToken = "",
            ),
            "RateLimited" to defaultClient.rateLimited(1, 1.seconds),
        )
    }
}

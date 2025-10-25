package krawler.server.player.brawlstars.test

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import krawler.RateLimiter
import krawler.server.player.brawlstars.BrawlStarsApiClient
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BrawlStarsApiClientUnitTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `getPlayer should call rateLimiter and return JsonObject`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } returns Unit
        val client = BrawlStarsApiClient(
            engine = MockEngine {
                respond(
                    content = """{"name":"Test"}""",
                    headers = headersOf("Content-Type" to listOf("application/json"))
                )
            },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN
        val result = client.getPlayer(tag = "#TEST")

        // THEN
        coVerify(exactly = 1) { rateLimiter.acquire() }
        assertTrue(result.isSuccess)
        assertEquals("Test", result.getOrThrow()!!["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun `getPlayer should return null on 404`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } returns Unit
        val client = BrawlStarsApiClient(
            engine = MockEngine { respondError(HttpStatusCode.NotFound) },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN
        val result = client.getPlayer(tag = "#NOTFOUND")

        // THEN
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `getPlayer should propagate CancellationException`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } throws CancellationException("Cancelled")
        val client = BrawlStarsApiClient(
            engine = MockEngine { respond("""{"name":"Test"}""") },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN / THEN
        assertFailsWith<CancellationException> {
            client.getPlayer(tag = "#TEST").getOrThrow()
        }
        coVerify(exactly = 1) { rateLimiter.acquire() }
    }

    @Test
    fun `getPlayerBattlelog should call rateLimiter and return JsonObject`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } returns Unit
        val client = BrawlStarsApiClient(
            engine = MockEngine {
                respond(
                    content = "{\"items\":[1, 2]}",
                    headers = headersOf("Content-Type" to listOf("application/json")),
                )
            },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN
        val result = client.getPlayerBattlelog(tag = "#TEST")

        // THEN
        coVerify(exactly = 1) { rateLimiter.acquire() }
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow()!!["items"]!!.jsonArray.size)
    }

    @Test
    fun `getPlayerBattlelog should return null on 404`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } returns Unit
        val client = BrawlStarsApiClient(
            engine = MockEngine { respondError(HttpStatusCode.NotFound) },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN
        val result = client.getPlayerBattlelog(tag = "#NOTFOUND")

        // THEN
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `getPlayerBattlelog should propagate CancellationException`() = runTest {
        // GIVEN
        val rateLimiter = mockk<RateLimiter>()
        coEvery { rateLimiter.acquire() } throws CancellationException("Cancelled")
        val client = BrawlStarsApiClient(
            engine = MockEngine { respond("{\"battles\":[]}") },
            bearerToken = "token",
            json = json,
            rateLimiter = rateLimiter
        )

        // WHEN / THEN
        assertFailsWith<CancellationException> {
            client.getPlayerBattlelog(tag = "#TEST").getOrThrow()
        }
        coVerify(exactly = 1) { rateLimiter.acquire() }
    }
}

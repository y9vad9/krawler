package krawler.server.player.brawlstars.integrationtest

import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import krawler.RateLimiter
import krawler.server.player.brawlstars.BrawlStarsApiClient
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class BrawlStarsApiClientIntegrationTest {

    private val bearerToken = System.getenv("BRAWLSTARS_API_KEY") ?: error("Set BRAWLSTARS_API_KEY env variable")
    private val rateLimiter = RateLimiter(maxRequests = 5, refillPeriod = 1.seconds)
    private val json = Json { ignoreUnknownKeys = true }

    private val client = BrawlStarsApiClient.createWithRoyalProxy(
        engine = CIO.create(),
        bearerToken = bearerToken,
        rateLimiter = rateLimiter,
        json = json
    )

    @Test
    fun `getPlayer should return valid JSON object for existing player tag`() = runTest {
        // GIVEN
        val tag = "#VLQPVPY"

        // WHEN
        val result = client.getPlayer(tag = tag)

        // THEN
        assertTrue(result.isSuccess, "Expected getPlayer to succeed")
        val playerJson: JsonObject = assertNotNull(result.getOrThrow())
        assertNotNull(playerJson["name"], "Expected 'name' field in player JSON")
        assertNotNull(playerJson["trophies"], "Expected 'trophies' field in player JSON")
    }

    @Test
    fun `getPlayerBattlelog should return non-empty battle log for existing player tag`() = runTest {
        // GIVEN
        val tag = "#VLQPVPY"

        // WHEN
        val result = client.getPlayerBattlelog(tag = tag)

        // THEN
        assertTrue(result.isSuccess, "Expected getPlayerBattlelog to succeed")
        val battleLogJson: JsonObject = assertNotNull(result.getOrThrow())
        assertNotNull(battleLogJson["items"], "Expected 'items' array in battle log JSON")
    }

    @Test
    fun `getPlayer should fail for non-existent player tag`() = runTest {
        // GIVEN
        val tag = "#NONEXISTENT"

        // WHEN
        val result = client.getPlayer(tag = tag)

        // THEN
        assertFalse(result.isFailure, "Expected getPlayer not to fail for non-existent tag")
        assertNull(result.getOrThrow())
    }

    @Test
    fun `getPlayerBattlelog should fail for non-existent player tag`() = runTest {
        // GIVEN
        val tag = "#NONEXISTENT"

        // WHEN
        val result = client.getPlayerBattlelog(tag = tag)

        // THEN
        assertFalse(result.isFailure, "Expected getPlayer not to fail for non-existent tag")
        assertNull(result.getOrThrow())
    }
}

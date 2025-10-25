package krawler.server.player.brawlstars

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import krawler.RateLimiter
import krawler.result.runCatchingSuspend

/**
 * A lightweight API client for interacting with the official Brawl Stars API or the RoyaleAPI proxy.
 *
 * This client provides simple coroutine-based methods for fetching player data and battle logs,
 * automatically handling rate limiting and JSON serialization.
 *
 * @param engine The [HttpClientEngine] used for performing network requests.
 * @param bearerToken The Brawl Stars API access token used for authentication.
 * @param json The [Json] instance for serialization and deserialization.
 * @param rateLimiter The [RateLimiter] instance controlling API request throughput.
 * @param baseUrl The base URL of the API endpoint. Defaults to the official Brawl Stars API.
 * @param configBlock An optional configuration block for customizing the underlying [HttpClient].
 */
class BrawlStarsApiClient(
    engine: HttpClientEngine,
    bearerToken: String,
    json: Json,
    private val rateLimiter: RateLimiter,
    baseUrl: String = "https://api.brawlstars.com/v1/",
    configBlock: HttpClientConfig<*>.() -> Unit = {},
) {
    companion object {
        /**
         * Creates and configures a new [BrawlStarsApiClient] instance for accessing the
         * official Brawl Stars API.
         *
         * @param engine The [HttpClientEngine] used for performing network requests.
         * @param bearerToken The Brawl Stars API access token used for authentication.
         * @param rateLimiter The [RateLimiter] controlling request frequency to the API server.
         * @param json The [Json] instance for serialization and deserialization.
         * @return A configured [BrawlStarsApiClient] ready for API interaction.
         */
        fun create(
            engine: HttpClientEngine,
            bearerToken: String,
            rateLimiter: RateLimiter,
            json: Json,
        ): BrawlStarsApiClient {
            return BrawlStarsApiClient(
                engine = engine,
                bearerToken = bearerToken,
                json = json,
                rateLimiter = rateLimiter,
            )
        }

        /**
         * Creates and configures a [BrawlStarsApiClient] that uses the RoyaleAPI proxy
         * (`https://bsproxy.royaleapi.dev/v1/`) instead of the official Brawl Stars API.
         *
         * The RoyaleAPI proxy can simplify API access and reduce rate-limit restrictions.
         * See the documentation at [https://docs.royaleapi.com/proxy.html](https://docs.royaleapi.com/proxy.html).
         *
         * @param engine The [HttpClientEngine] used for performing network requests.
         * @param bearerToken The Brawl Stars API access token used for authentication.
         * @param rateLimiter The [RateLimiter] controlling request frequency to the proxy server.
         * @param json The [Json] instance for serialization and deserialization.
         * @return A configured [BrawlStarsApiClient] that communicates via the RoyaleAPI proxy.
         */
        fun createWithRoyalProxy(
            engine: HttpClientEngine,
            bearerToken: String,
            rateLimiter: RateLimiter,
            json: Json,
        ): BrawlStarsApiClient {
            return BrawlStarsApiClient(
                baseUrl = "https://bsproxy.royaleapi.dev/v1/",
                engine = engine,
                bearerToken = bearerToken,
                json = json,
                rateLimiter = rateLimiter,
            )
        }
    }

    /** The internal [HttpClient] instance used for all network operations. */
    private val client: HttpClient = HttpClient(engine) {
        expectSuccess = true

        defaultRequest {
            url(baseUrl)
            accept(ContentType.Application.Json)
            bearerAuth(bearerToken)
        }

        install(ContentNegotiation) {
            json(json)
        }

        configBlock()
    }

    /**
     * Fetches the player profile by their unique [tag].
     *
     * This method respects the configured [RateLimiter] and automatically delays requests
     * when the rate limit is reached. If the player does not exist (HTTP 404), it returns `null`.
     *
     * @param tag The player tag, including the leading '#'.
     * @return A [Result] wrapping the raw [JsonObject] response if found, `null` if the player
     *         does not exist, or an exception if the request fails for other reasons.
     */
    suspend fun getPlayer(tag: String): Result<JsonObject?> = runCatchingSuspend {
        rateLimiter.acquire()
        client.get {
            url {
                path("players", tag)
            }
        }.body<JsonObject>()
    }.recoverCatching { e ->
        if (e is ClientRequestException && e.response.status == HttpStatusCode.NotFound)
            null
        else throw e
    }

    /**
     * Fetches the most recent battle log entries for the specified player [tag].
     *
     * Each call returns a paginated list of recent battles as JSON. Requests are subject
     * to [RateLimiter] enforcement to prevent exceeding API quotas. If the player does
     * not exist or has no battle logs (HTTP 404), it returns `null`.
     *
     * @param tag The player tag, including the leading '#'.
     * @return A [Result] containing the [JsonObject] battle log data if available, `null`
     *         if no data is found, or an exception if the request fails for other reasons.
     */
    suspend fun getPlayerBattlelog(tag: String): Result<JsonObject?> = runCatchingSuspend {
        rateLimiter.acquire()
        client.get {
            url {
                path("players", tag, "battlelog")
            }
        }.body<JsonObject>()
    }.recoverCatching { e ->
        if (e is ClientRequestException && e.response.status == HttpStatusCode.NotFound)
            null
        else throw e
    }
}

package krawler.server.brawlstars.client

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import krawler.server.brawlstars.client.BrawlStarsApiClient.Companion.DEFAULT_COUNTRY_CODE
import krawler.server.brawlstars.client.BrawlStarsApiClient.Companion.create
import krawler.server.brawlstars.client.BrawlStarsApiClient.Companion.createWithRoyaleApiProxy
import krawler.server.brawlstars.client.battle.RawBattleRecord
import krawler.server.brawlstars.client.club.RawClub
import krawler.server.brawlstars.client.club.RawClubMember
import krawler.server.brawlstars.client.event.RawScheduledEvent
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.player.RawPlayer
import krawler.server.brawlstars.client.ranking.RawClubRanking
import krawler.server.brawlstars.client.ranking.RawPlayerRanking
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * # Brawl Stars API Client
 *
 * Provides access to the [Brawl Stars REST API](https://developer.brawlstars.com).
 * All functions are **suspending** and return results wrapped in [Result],
 * with the successful value containing the requested data or `null` if not found.
 *
 * ## Pagination
 * Many endpoints support pagination using `after` and `before` cursors.
 * These cursors can be retrieved from the `paging` property of a previous response,
 * such as [RawPaginatedList].
 *
 * ## Implementations
 * This interface can be instantiated via:
 * - [create] — Uses the official API at `https://api.brawlstars.com`.
 * - [createWithRoyaleApiProxy] — Uses the RoyaleAPI proxy at `https://proxy.royaleapi.dev`.
 *
 * Both factory methods configure the client with:
 * - A provided [HttpClientEngine]
 * - An API access token
 * - A [Json] serializer (default: `ignoreUnknownKeys = true`)
 * - Optional extra [io.ktor.client.HttpClient] configuration
 */
interface BrawlStarsApiClient {
    companion object {
        /**
         * Default country code used in the rankings API.
         */
        const val DEFAULT_COUNTRY_CODE: String = "global"

        /**
         * Creates a [BrawlStarsApiClient] using the official Brawl Stars API.
         *
         * This client will communicate with `https://api.brawlstars.com`.
         *
         * @param engine The [HttpClientEngine] to use for network requests.
         * @param bearerToken Your Brawl Stars API access token.
         * @param json The [Json] instance used for serialization/deserialization. Defaults to
         *             [Json] with `ignoreUnknownKeys = true`.
         * @return A [BrawlStarsApiClient] configured to call the official Brawl Stars API.
         */
        fun create(
            engine: HttpClientEngine,
            bearerToken: String,
            json: Json = Json { ignoreUnknownKeys = true },
        ): BrawlStarsApiClient = DefaultRawBrawlStarsApiClient(
            engine = engine,
            bearerToken = bearerToken,
            baseUrl = "https://api.brawlstars.com/v1/"
        ) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        /**
         * Creates a [BrawlStarsApiClient] using the RoyaleAPI proxy.
         *
         * This client will use the proxy endpoint `https://proxy.royaleapi.dev` instead of the official API.
         * See the proxy documentation: [https://docs.royaleapi.com/proxy.html](https://docs.royaleapi.com/proxy.html)
         *
         * @param engine The [HttpClientEngine] to use for network requests.
         * @param bearerToken Your Brawl Stars API access token.
         * @param json The [Json] instance used for serialization/deserialization. Defaults to
         *             [Json] with `ignoreUnknownKeys = true`.
         * @return A [BrawlStarsApiClient] configured to call the RoyaleAPI proxy.
         */
        fun createWithRoyaleApiProxy(
            engine: HttpClientEngine,
            bearerToken: String,
            json: Json = Json { ignoreUnknownKeys = true },
        ): BrawlStarsApiClient = DefaultRawBrawlStarsApiClient(
            engine = engine,
            bearerToken = bearerToken,
            baseUrl = "https://bsproxy.royaleapi.dev/v1/"
        ) {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    /**
     * Retrieves a player by their tag.
     *
     * @param tag The unique tag of the player.
     * @return A [Result] wrapping the [RawPlayer] if found, or null if the player does not exist,
     *         or a failure if the request failed.
     */
    suspend fun getPlayer(tag: String): Result<RawPlayer?>

    /**
     * Retrieves the battle log for a specific player.
     *
     * The battle log contains recent battles the player participated in.
     *
     * @param tag The unique tag of the player.
     * @return A [Result] wrapping a list of battle records.
     */
    suspend fun getPlayerBattleLog(tag: String): Result<List<RawBattleRecord>?>

    /**
     * Retrieves a club by its tag.
     *
     * @param tag The unique tag of the club.
     * @return A [Result] wrapping the [RawClub] if found, or null if the club does not exist,
     *         or a failure if the request failed.
     */
    suspend fun getClub(tag: String): Result<RawClub?>

    /**
     * Retrieves the list of members for a club by its tag.
     *
     * @param tag The unique tag of the club.
     * @return A [Result] wrapping the list of [RawClubMember] objects, or a failure if the request failed.
     */
    suspend fun getClubMembers(tag: String): Result<List<RawClubMember>?>

    /**
     * Retrieves player rankings for a specific brawler.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param brawlerId The ID of the brawler.
     * @param countryCode The country code for rankings (default is [DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawPlayerRanking], or a failure if the request failed.
     */
    suspend fun getBrawlerRanking(
        brawlerId: Int,
        countryCode: String = DEFAULT_COUNTRY_CODE,
        after: String? = null,
        before: String? = null,
        limit: Int? = null,
    ): Result<RawPaginatedList<RawPlayerRanking>>

    /**
     * Retrieves overall player rankings for a specific brawler.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param countryCode The country code for rankings (default is [DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawPlayerRanking], or a failure if the request failed.
     */
    suspend fun getPlayerRanking(
        countryCode: String = DEFAULT_COUNTRY_CODE,
        after: String? = null,
        before: String? = null,
        limit: Int? = null,
    ): Result<RawPaginatedList<RawPlayerRanking>>

    /**
     * Retrieves club rankings.
     *
     * Supports pagination using the `after` and `before` cursors from a previous [RawPaginatedList].
     *
     * @param countryCode The country code for rankings (default is [DEFAULT_COUNTRY_CODE]).
     * @param after Optional cursor to return results **after** the given item, for pagination.
     * @param before Optional cursor to return results **before** the given item, for pagination.
     * @param limit Optional parameter to limit the number of results returned.
     * @return A [Result] wrapping a [RawPaginatedList] of [RawClubRanking], or a failure if the request failed.
     */
    suspend fun getClubRanking(
        countryCode: String = DEFAULT_COUNTRY_CODE,
        after: String? = null,
        before: String? = null,
        limit: Int? = null,
    ): Result<RawPaginatedList<RawClubRanking>>

    /**
     * Retrieves the scheduled event rotation in Brawl Stars.
     *
     * @return A list of [RawScheduledEvent] representing upcoming events.
     */
    suspend fun getEventsRotation(): Result<List<RawScheduledEvent>>
}

/**
 * Creates a new instance of [BrawlStarsApiClient] with the client-side rate limit.
 * It's an experimental API with possible performance drawbacks.
 *
 * @param maxRequests Maximum number of requests allowed per [per] duration.
 * @param per The time window for rate limiting.
 */
@OptIn(ExperimentalTime::class)
fun BrawlStarsApiClient.rateLimited(
    maxRequests: Int,
    per: Duration,
): BrawlStarsApiClient = RateLimitedRawBrawlStarsApiClient(this, maxRequests, per)

/**
 * Returns the player if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetPlayer(tag: String): RawPlayer? =
    getPlayer(tag).getOrNull()

/**
 * Returns the club if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetClub(tag: String): RawClub? =
    getClub(tag).getOrNull()

/**
 * Returns the club members list if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetClubMembers(tag: String): List<RawClubMember>? =
    getClubMembers(tag).getOrNull()

/**
 * Returns the brawler ranking if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetBrawlerRanking(
    brawlerId: Int,
    countryCode: String = DEFAULT_COUNTRY_CODE,
): List<RawPlayerRanking>? = getBrawlerRanking(brawlerId, countryCode).getOrNull()

/**
 * Returns the player ranking if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetPlayerRanking(
    countryCode: String = DEFAULT_COUNTRY_CODE,
): List<RawPlayerRanking>? = getPlayerRanking(countryCode).getOrNull()

/**
 * Returns the club ranking if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetClubRanking(
    countryCode: String = DEFAULT_COUNTRY_CODE,
): List<RawClubRanking>? = getClubRanking(countryCode).getOrNull()

/**
 * Returns the club ranking if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetEventsRotation(): List<RawScheduledEvent>? =
    getEventsRotation().getOrNull()

/**
 * Returns the player battle log if successful, or null in case of error.
 */
suspend fun BrawlStarsApiClient.tryGetPlayerBattleLog(tag: String): List<RawBattleRecord>? =
    getPlayerBattleLog(tag).getOrNull()

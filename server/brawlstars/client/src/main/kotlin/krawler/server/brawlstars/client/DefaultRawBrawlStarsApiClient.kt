@file:Suppress("detekt.StringLiteralDuplication")

package krawler.server.brawlstars.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.http.path
import kotlinx.io.IOException
import krawler.server.brawlstars.client.BrawlStarsApiClient.Companion
import krawler.server.brawlstars.client.battle.RawBattleRecord
import krawler.server.brawlstars.client.club.RawClub
import krawler.server.brawlstars.client.club.RawClubMember
import krawler.server.brawlstars.client.event.RawScheduledEvent
import krawler.server.brawlstars.client.pagination.RawPaginatedList
import krawler.server.brawlstars.client.player.RawPlayer
import krawler.server.brawlstars.client.ranking.RawClubRanking
import krawler.server.brawlstars.client.ranking.RawPlayerRanking

/**
 * Default implementation of [BrawlStarsApiClient] using Ktor's [HttpClient] to interact
 * with the official Brawl Stars API.
 *
 * This client:
 * - Configures a base URL and default JSON content negotiation.
 * - Attaches a Bearer token for authentication to every request.
 * - Automatically maps HTTP errors to specific [BrawlStarsApiException] subclasses.
 * - Provides convenience methods for retrieving players, clubs, battles, rankings, and events.
 *
 * @param engine The [HttpClientEngine] to be used for network communication.
 * @param bearerToken The Bearer token used for authenticating requests to the Brawl Stars API.
 * @param baseUrl The base URL for the Brawl Stars API (defaults to `https://api.brawlstars.com/v1/`).
 * @param configBlock Additional [HttpClientConfig] customization applied after default configuration.
 */
class DefaultRawBrawlStarsApiClient(
    engine: HttpClientEngine,
    bearerToken: String,
    baseUrl: String = "https://api.brawlstars.com/v1/",
    configBlock: HttpClientConfig<*>.() -> Unit,
) : BrawlStarsApiClient {
    private val client: HttpClient = HttpClient(engine) {
        defaultRequest {
            url(baseUrl)
            accept(ContentType.Application.Json)

            bearerAuth(bearerToken)
        }

        configBlock()
    }


    /**
     * Retrieves a [RawPlayer] by their tag.
     *
     * @param tag The player's unique tag (with the leading '#').
     * @return A [Result] containing the player if found, `null` if not found, or a failure if the request failed.
     */
    override suspend fun getPlayer(tag: String): Result<RawPlayer?> =
        getRequest("players", tag)

    /**
     * Retrieves the battle log for a specific player.
     *
     * The battle log contains recent battles the player participated in.
     *
     * @param tag The player's unique tag (with the leading '#').
     * @return A [Result] containing the list of [RawBattleRecord] entries, or `null` if no battles are found.
     */
    override suspend fun getPlayerBattleLog(tag: String): Result<List<RawBattleRecord>?> =
        getRequest<RawPaginatedList<RawBattleRecord>?>("players", tag, "battlelog")

    /**
     * Retrieves a [RawClub] by its tag.
     *
     * @param tag The club's unique tag (with the leading '#').
     * @return A [Result] containing the club if found, `null` if not found, or a failure if the request failed.
     */
    override suspend fun getClub(tag: String): Result<RawClub?> =
        getRequest("clubs", tag)

    /**
     * Retrieves the list of members for a given club.
     *
     * @param tag The club's unique tag (with the leading '#').
     * @return A [Result] containing the list of [RawClubMember] entries, or a failure if the request failed.
     */
    override suspend fun getClubMembers(tag: String): Result<List<RawClubMember>?> =
        getRequest<RawPaginatedList<RawClubMember>>("clubs", tag, "members")

    /**
     * Retrieves player rankings for a specific brawler.
     *
     * Supports pagination via the `after` and `before` cursors.
     *
     * @param brawlerId The brawler's ID.
     * @param countryCode The country code for rankings (default is [Companion.DEFAULT_COUNTRY_CODE]).
     * @param after Cursor for results after a given item.
     * @param before Cursor for results before a given item.
     * @param limit Maximum number of results to return.
     * @return A [Result] containing a [RawPaginatedList] of [RawPlayerRanking].
     */
    override suspend fun getBrawlerRanking(
        brawlerId: Int,
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawPlayerRanking>> = getRequest<RawPaginatedList<RawPlayerRanking>>(
        "rankings", countryCode, "brawlers", brawlerId.toString(),
    ) {
        parameters {
            if (after != null) append("after", after)
            if (before != null) append("before", before)
            if (limit != null) append("limit", limit.toString())
        }
    }.mapCatching { it ?: error("`getBrawlerRanking` returned unexpected null") }

    /**
     * Retrieves overall player rankings.
     *
     * Supports pagination via the `after` and `before` cursors.
     *
     * @param countryCode The country code for rankings.
     * @param after Cursor for results after a given item.
     * @param before Cursor for results before a given item.
     * @param limit Maximum number of results to return.
     * @return A [Result] containing a [RawPaginatedList] of [RawPlayerRanking].
     */
    override suspend fun getPlayerRanking(
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawPlayerRanking>> = getRequest<RawPaginatedList<RawPlayerRanking>>(
        "rankings", countryCode, "players"
    ) {
        parameters {
            if (after != null) append("after", after)
            if (before != null) append("before", before)
            if (limit != null) append("limit", limit.toString())
        }
    }.mapCatching { it ?: error("`getPlayerRanking` returned unexpected null") }

    /**
     * Retrieves club rankings.
     *
     * Supports pagination via the `after` and `before` cursors.
     *
     * @param countryCode The country code for rankings.
     * @param after Cursor for results after a given item.
     * @param before Cursor for results before a given item.
     * @param limit Maximum number of results to return.
     * @return A [Result] containing a [RawPaginatedList] of [RawClubRanking].
     */
    override suspend fun getClubRanking(
        countryCode: String,
        after: String?,
        before: String?,
        limit: Int?
    ): Result<RawPaginatedList<RawClubRanking>> = getRequest<RawPaginatedList<RawClubRanking>>(
        "rankings", countryCode, "clubs"
    ) {
        parameters {
            if (after != null) append("after", after)
            if (before != null) append("before", before)
            if (limit != null) append("limit", limit.toString())
        }
    }.mapCatching { it ?: error("`getClubRanking` returned unexpected null") }

    /**
     * Retrieves the scheduled event rotation.
     *
     * @return A [Result] containing a list of [RawScheduledEvent] representing upcoming events.
     */
    override suspend fun getEventsRotation(): Result<List<RawScheduledEvent>> =
        getRequest<List<RawScheduledEvent>>("events", "rotation").mapCatching {
            it ?: error("`getEventRotation` returned unexpected null")
        }

    private suspend inline fun <reified T> getRequest(
        vararg pathSegments: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): Result<T?> = runCatching {
        val result = client.get {
            url {
                path(*pathSegments)
            }

            block()
        }

        if (result.status.isSuccess())
            return Result.success(result.body<T>())

        when (result.status) {
            HttpStatusCode.NotFound -> null
            HttpStatusCode.BadRequest -> throw BadRequestException(result.body())
            HttpStatusCode.Forbidden -> throw AccessDeniedException(result.body())
            HttpStatusCode.TooManyRequests -> throw LimitsExceededException(result.body())
            HttpStatusCode.InternalServerError -> throw InternalServerErrorException(result.body())
            HttpStatusCode.ServiceUnavailable -> throw UnderMaintenanceException(result.body())
            else -> throw IOException(
                "Brawl Stars API returned a '${result.status.value}' status code with the " +
                        "following response: ${result.bodyAsText()}"
            )
        }
    }
}

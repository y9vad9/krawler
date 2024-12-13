package com.y9vad9.bcm.data.brawlstars

import com.y9vad9.bcm.core.brawlstars.entity.brawler.Brawler
import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.BrawlerId
import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.event.Battle
import com.y9vad9.bcm.core.brawlstars.entity.event.ScheduledEvent
import com.y9vad9.bcm.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.exception.ClientError
import com.y9vad9.bcm.core.common.entity.value.Count
import com.y9vad9.bcm.core.common.entity.value.CountryCode
import com.y9vad9.bcm.data.brawlstars.pagination.Cursors
import com.y9vad9.bcm.data.brawlstars.pagination.Page
import com.y9vad9.bcm.data.brawlstars.pagination.PagesIterator
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * A client for interacting with the Brawl Stars API.
 *
 * This client provides methods for retrieving player information, battle logs, and club details.
 * It utilizes the provided bearer token for authentication and is designed for asynchronous operations.
 *
 * @property bearerToken The token used for API authentication.
 * @property json The JSON configuration for parsing API responses. Defaults to ignoring unknown keys.
 * @property configBlock Optional HTTP client configuration.
 */
class BrawlStarsClient(
    bearerToken: String,
    json: Json = Json { ignoreUnknownKeys = true },
    configBlock: HttpClientConfig<CIOEngineConfig>.() -> Unit = {},
) {
    private val client: HttpClient = HttpClient(CIO) {
        HttpClient(CIO) {
            defaultRequest {
                url("https://api.brawlstars.com/v1")
                accept(ContentType.Application.Json)

                bearerAuth(bearerToken)
            }

            install(ContentNegotiation) {
                json(json)
            }
        }
        configBlock()
    }

    /**
     * Retrieves information about a player.
     *
     * @param tag The unique player tag (e.g., #PLAYER_TAG).
     * @return [Result] containing the [Player] object if successful, or null if the player was not found.
     */
    suspend fun getPlayer(tag: PlayerTag): Result<Player?> =
        getRequest(typeInfo<Player>(), "v1/players/${tag.toString().replace("#", "%23")}")

    /**
     * Retrieves a player's battle log, showing recent battles.
     *
     * **Note:** New battles may take up to 30 minutes to appear in the battle log.
     *
     * @param tag The unique player tag (e.g., #PLAYER_TAG).
     * @return [Result] containing a list of [Battle] objects if successful, or null if the player was not found.
     */
    suspend fun getPlayerBattlelog(tag: PlayerTag): Result<List<Battle>?> =
        getRequest<GetBattleListResponse>(
            typeInfo<GetBattleListResponse>(),
            "v1/players/${tag.toString().replace("#", "%23")}/battlelog"
        )
            .map { it?.battles }

    /**
     * Retrieves information about a club.
     *
     * @param tag The unique club tag (e.g., #CLUB_TAG).
     * @return [Result] containing the [Club] object if successful, or null if the club was not found.
     */
    suspend fun getClub(tag: ClubTag): Result<Club?> =
        getRequest(typeInfo<Club>(), "v1/clubs/${tag.toString().replace("#", "%23")}")

    /**
     * Retrieves the list of members in a club.
     *
     * @param tag The unique club tag (e.g., #CLUB_TAG).
     * @return [Result] containing a [ClubMember] object if successful, or null if the club was not found.
     */
    suspend fun getClubMembers(tag: ClubTag): Result<ClubMember?> = getRequest(
        typeInfo = typeInfo<Club>(),
        url = "v1/clubs/${tag.toString().replace("#", "%23")}/members"
    )

    /**
     * Provides a lazy iterator for paginated retrieval of club members.
     *
     * @param tag The unique club tag (e.g., #CLUB_TAG).
     * @param limit The maximum number of members to retrieve per page.
     * @return [PagesIterator] that iterates through paginated [ClubMember] objects.
     */
    fun getClubMembersLazily(tag: ClubTag, limit: Count): PagesIterator<ClubMember> {
        return PagesIterator(limit = limit) { count, cursors ->
            getRequestListWithPagination(
                url = "v1/clubs/${tag.toString().replace("#", "%23")}/members",
                cursors = cursors,
                limit = count,
                typeInfo = typeInfo<ClubMember>()
            )
        }
    }

    /**
     * Retrieves a list of all brawlers.
     *
     * @return [Result] containing a list of [BrawlerView] objects.
     */
    suspend fun getBrawlers(): Result<List<Brawler.View>> {
        return getRequest<Page<Brawler.View>>(typeInfo<Page<Brawler.View>>(), "v1/brawlers")
            .map { it!!.items!! } // no kaboom expected: API should never return 404
    }

    /**
     * Retrieves information about a specific brawler.
     *
     * @param id The unique ID of the brawler.
     * @return [Result] containing the [BrawlerView] object if successful, or null if the brawler was not found.
     */
    suspend fun getBrawler(id: BrawlerId): Result<Brawler.View?> {
        return getRequest<Brawler.View>(typeInfo<Page<Brawler.View>>(), "v1/brawlers/${id.value}")
    }

    /**
     * Provides a lazy iterator for paginated retrieval of brawlers.
     *
     * @param limit The maximum number of brawlers to retrieve per page.
     * @return [PagesIterator] that iterates through paginated [BrawlerView] objects.
     */
    fun getBrawlersLazily(limit: Count): PagesIterator<Brawler.View> {
        return PagesIterator<Brawler.View>(limit) { count, cursors ->
            getRequestListWithPagination("v1/brawlers", cursors, limit, typeInfo<Brawler.View>())
        }
    }

    /**
     * Retrieves a list of club rankings for a specific country.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @return [Result] containing a list of [Club.Ranking] objects if successful.
     */
    suspend fun getClubsRankings(countryCode: CountryCode = CountryCode.GLOBAL): Result<List<Club.Ranking>> {
        return getRequest<List<Club.Ranking>>(
            typeInfo = typeInfo<List<Club.Ranking>>(),
            url = "v1/rankings/${countryCode.value}/clubs",
        ).map { it!! }
    }

    /**
     * Provides a lazy iterator for paginated retrieval of club rankings.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @param limit The maximum number of rankings to retrieve per page.
     * @return [PagesIterator] that iterates through paginated [Club.Ranking] objects.
     */
    fun getClubsRankingsLazily(
        countryCode: CountryCode = CountryCode.GLOBAL,
        limit: Count,
    ): PagesIterator<Club.Ranking> {
        return PagesIterator<Club.Ranking>(limit) { count, cursors ->
            getRequestListWithPagination(
                typeInfo = typeInfo<List<Club.Ranking>>(),
                url = "v1/rankings/${countryCode.value}/clubs",
                cursors = cursors,
                limit = count,
            )
        }
    }

    /**
     * Retrieves a list of player rankings for a specific country.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @return [Result] containing a list of [Player.Ranking] objects if successful.
     */
    suspend fun getPlayersRankings(countryCode: CountryCode = CountryCode.GLOBAL): Result<List<Player.Ranking>> {
        return getRequest<List<Player.Ranking>>(
            typeInfo = typeInfo<List<Player.Ranking>>(),
            url = "v1/rankings/${countryCode.value}/players",
        ).map { it!! }
    }

    /**
     * Provides a lazy iterator for paginated retrieval of player rankings.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @param limit The maximum number of rankings to retrieve per page.
     * @return [PagesIterator] that iterates through paginated [Player.Ranking] objects.
     */
    fun getPlayersRankingsLazily(
        countryCode: CountryCode = CountryCode.GLOBAL,
        limit: Count,
    ): PagesIterator<Player.Ranking> {
        return PagesIterator<Player.Ranking>(limit) { count, cursors ->
            getRequestListWithPagination(
                typeInfo = typeInfo<List<Player.Ranking>>(),
                url = "v1/rankings/${countryCode.value}/players",
                cursors = cursors,
                limit = count,
            )
        }
    }

    /**
     * Retrieves rankings for a specific brawler in a specific country.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @param brawlerId The unique identifier of the brawler.
     * @return [Result] containing a list of [Brawler.Ranking] objects if successful.
     * @see BrawlerId.Companion
     */
    suspend fun getBrawlerRankings(
        countryCode: CountryCode = CountryCode.GLOBAL,
        brawlerId: BrawlerId,
    ): Result<List<Brawler.Ranking>> {
        return getRequest<List<Brawler.Ranking>>(
            typeInfo = typeInfo<List<Brawler.Ranking>>(),
            url = "v1/rankings/${countryCode.value}/brawlers/${brawlerId.value}",
        ).map { it!! }
    }

    /**
     * Provides a lazy iterator for paginated retrieval of brawler rankings.
     *
     * @param countryCode The country code to filter rankings (default: GLOBAL).
     * @param brawlerId The unique identifier of the brawler.
     * @param limit The maximum number of rankings to retrieve per page.
     * @return [PagesIterator] that iterates through paginated [Brawler.Ranking] objects.
     * @see BrawlerId.Companion
     */
    fun getBrawlerRankingsLazily(
        countryCode: CountryCode = CountryCode.GLOBAL,
        brawlerId: BrawlerId,
        limit: Count,
    ): PagesIterator<Brawler.Ranking> {
        return PagesIterator<Brawler.Ranking>(limit) { count, cursors ->
            getRequestListWithPagination(
                typeInfo = typeInfo<List<Brawler.Ranking>>(),
                url = "v1/rankings/${countryCode.value}/brawlers/${brawlerId.value}",
                cursors = cursors,
                limit = count,
            )
        }
    }

    /**
     * Get event rotation for ongoing events.
     */
    suspend fun getEventRotation(): Result<List<ScheduledEvent>> = getRequest<List<ScheduledEvent>>(
        typeInfo = typeInfo<List<ScheduledEvent>>(),
        url = "v1/events/rotation",
    ).map { it!! }

    private suspend fun <T> getRequestListWithPagination(
        url: String,
        cursors: Cursors?,
        limit: Count,
        typeInfo: TypeInfo,
    ): Result<Page<T>> = getRequest<Page<ClubMember>>(
        typeInfo = typeInfo,
        url = url,
    ) {
        parameters {
            append("limit", limit.value.toString())
            if (cursors?.after != null)
                append("after", cursors.after.value)

            if (cursors?.before != null)
                append("after", cursors.before.value)
        }
    }.map {
        @Suppress("UNCHECKED_CAST")
        it!! as Page<T>
    }

    private suspend fun <T> getRequest(
        typeInfo: TypeInfo,
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): Result<T?> =
        runCatching {
            val result = client.get(url) {
                block()
            }

            return@runCatching if (result.status.isSuccess()) {
                result.body<T>(typeInfo)
            } else if (result.status == HttpStatusCode.NotFound) {
                null
            } else {
                throw result.body<ClientError>()
            }
        }

    @Serializable
    private data class GetBattleListResponse(
        val battles: List<Battle>,
    )
}
package com.y9vad9.bcm.data.brawlstars

import com.y9vad9.bcm.data.brawlstars.entity.BsBattle
import com.y9vad9.bcm.data.brawlstars.entity.BsClub
import com.y9vad9.bcm.data.brawlstars.entity.BsMember
import com.y9vad9.bcm.data.brawlstars.entity.BsPlayer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class BrawlStarsClient(
    bearerToken: String,
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
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        configBlock()
    }

    suspend fun getPlayer(tag: String): Result<BsPlayer?> = runCatching {
        val result = client.get("v1/players/${tag.replace("#", "%23")}")

        if (result.status == HttpStatusCode.NotFound)
            return@runCatching null
        else result.body()
    }

    suspend fun getPlayerBattleLog(tag: String): Result<List<BsBattle>?> = runCatching {
        val result = client.get("v1/players/${tag.replace("#", "%23")}/battlelog")

        if (result.status == HttpStatusCode.NotFound)
            return@runCatching null
        else result.body<BattleList>().battles
    }
    suspend fun getClub(tag: String): Result<BsClub?> = runCatching {
        val result = client.get("v1/clubs/${tag.replace("#", "%23")}")

        if (result.status == HttpStatusCode.NotFound)
            return@runCatching null
        else result.body()
    }
    suspend fun getClubMembers(tag: String): Result<List<BsMember>?> = runCatching {
        val result = client.get("v1/clubs/${tag.replace("#", "%23")}/members")

        if (result.status == HttpStatusCode.NotFound)
            return@runCatching null
        else result.body<GetMembersListResponse>().items
    }

    @Serializable
    private data class GetMembersListResponse(
        val items: List<BsMember>,
    )

    @Serializable
    private data class BattleList(
        val battles: List<BsBattle>,
    )
}
package com.y9vad9.gamerslegion.brawlstars

import com.y9vad9.gamerslegion.brawlstars.entity.Battle
import com.y9vad9.gamerslegion.brawlstars.entity.Club
import com.y9vad9.gamerslegion.brawlstars.entity.Member
import com.y9vad9.gamerslegion.brawlstars.entity.Player
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class BrawlStarsClient(
    private val client: HttpClient,
) {
    suspend fun getPlayer(tag: String): Player = client.get("v1/players/${tag.replace("#", "%23")}").body()
    suspend fun getPlayerBattleLog(tag: String) = client.get("v1/players/${tag.replace("#", "%23")}/battlelog").body<BattleList>().battles
    suspend fun getClub(tag: String): Club = client.get("v1/clubs/${tag.replace("#", "%23")}").body()
    suspend fun getClubMembers(tag: String): List<Member> =
        client.get("v1/clubs/${tag.replace("#", "%23")}/members").body<GetMembersListResponse>().items

    @Serializable
    private data class GetMembersListResponse(
        val items: List<Member>,
    )

    @Serializable
    private data class BattleList(
        val battles: List<Battle>,
    )

}
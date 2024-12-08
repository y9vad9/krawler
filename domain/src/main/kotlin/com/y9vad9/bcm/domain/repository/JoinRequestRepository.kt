package com.y9vad9.bcm.domain.repository

import com.y9vad9.bcm.domain.entity.JoinRequest
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.Count
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface JoinRequestRepository {
    suspend fun undecidedCount(): Count
    suspend fun hasAnyFrom(userId: TelegramUserId): Boolean

    suspend fun create(request: JoinRequest)
    suspend fun removeFrom(userId: TelegramUserId)

    suspend fun getUndecided(club: ClubTag?, maxSize: Count): List<JoinRequest>
    suspend fun getRequest(id: Uuid): JoinRequest?

    suspend fun accept(uuid: Uuid)
    suspend fun decline(uuid: Uuid, message: CustomMessage)
}
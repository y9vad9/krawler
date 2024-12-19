package com.y9vad9.starix.core.system.repository

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.common.entity.value.Count
import com.y9vad9.starix.core.common.entity.value.CustomMessage
import com.y9vad9.starix.core.system.entity.JoinRequest
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
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

    suspend fun accept(uuid: Uuid, message: CustomMessage)
    suspend fun decline(uuid: Uuid, message: CustomMessage)
}
package com.y9vad9.bcm.data

import com.y9vad9.bcm.data.database.JoinRequestsTable
import com.y9vad9.bcm.domain.entity.JoinRequest
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.Count
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import com.y9vad9.bcm.domain.repository.JoinRequestRepository
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import kotlin.uuid.Uuid

class JoinRequestRepositoryImpl(
    private val joinRequestsTable: JoinRequestsTable,
) : JoinRequestRepository {
    @OptIn(ValidationDelicateApi::class)
    override suspend fun undecidedCount(): Count {
        return joinRequestsTable.count(JoinRequestsTable.Status.UNDECIDED)
            .let { Count.createUnsafe(it.toInt()) }
    }

    override suspend fun hasAnyFrom(userId: TelegramUserId): Boolean {
        return joinRequestsTable.hasAnyFromTgId(userId.value)
    }

    override suspend fun create(request: JoinRequest) {
        joinRequestsTable.create(
            id = request.id,
            playerTag = request.playerTag.toString(),
            clubTag = request.clubTag.toString(),
            message = request.message.toString(),
            tgId = request.tgId.value,
        )
    }

    override suspend fun removeFrom(userId: TelegramUserId) {
        joinRequestsTable.deleteByTelegramId(userId.value)
    }

    override suspend fun getUndecided(
        club: ClubTag?,
        maxSize: Count,
    ): List<JoinRequest> {
        return joinRequestsTable.getList(JoinRequestsTable.Status.UNDECIDED, maxSize.value)
    }

    override suspend fun getRequest(id: Uuid): JoinRequest? {
        return joinRequestsTable.get(id)
    }

    override suspend fun accept(uuid: Uuid) {
        joinRequestsTable.updateStatus(uuid, JoinRequestsTable.Status.ACCEPTED)
    }

    override suspend fun decline(uuid: Uuid, message: CustomMessage) {
        joinRequestsTable.updateStatus(uuid, JoinRequestsTable.Status.DECLINED)
    }

}
package com.y9vad9.bcm.domain.usecase.join_request

import com.y9vad9.bcm.domain.entity.brawlstars.canKickPlayersInClub
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.CustomMessage
import com.y9vad9.bcm.domain.repository.JoinRequestRepository
import com.y9vad9.bcm.domain.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DeclineJoinRequestsUseCase(
    val userRepository: UserRepository,
    val joinRequests: JoinRequestRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        id: TelegramUserId,
        requestId: Uuid,
        message: CustomMessage,
    ): Result {
        val member = userRepository.getById(id)
        val request = joinRequests.getRequest(requestId)

        val hasPermission = member.bsPlayers?.firstOrNull { player ->
            player.club.tag.toString() == request.clubTag.toString()
        }?.canKickPlayersInClub == true

        if (!hasPermission) return Result.AccessDenied

        joinRequests.decline(requestId, message)
        return Result.Success
    }

    sealed interface Result {
        data object AccessDenied : Result
        data object Success : Result
    }
}
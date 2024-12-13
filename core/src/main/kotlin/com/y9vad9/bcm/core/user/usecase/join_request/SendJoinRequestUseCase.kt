package com.y9vad9.bcm.core.user.usecase.join_request

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.user.entity.JoinRequest
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.user.repository.JoinRequestRepository
import com.y9vad9.bcm.core.user.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SendJoinRequestUseCase(
    private val joinRequests: JoinRequestRepository,
    private val members: UserRepository,
    private val maxRequests: Int,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        id: TelegramUserId,
        tag: PlayerTag,
        clubTag: ClubTag,
        message: CustomMessage,
    ): Result {
        if (joinRequests.undecidedCount().value >= maxRequests)
            return Result.TooManyRequests

        if (joinRequests.hasAnyFrom(id))
            return Result.AlreadySent

        joinRequests.create(
            request = JoinRequest(
                id = Uuid.random(),
                playerTag = tag,
                message = message,
                clubTag = clubTag,
                tgId = id,
            )
        )

        return Result.Success
    }

    sealed interface Result {
        data object TooManyRequests : Result
        data object AlreadySent : Result
        data object Success : Result
    }
}
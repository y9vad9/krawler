package com.y9vad9.bcm.domain.usecase.join_request

import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.repository.JoinRequestRepository
import com.y9vad9.bcm.domain.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class RevokeJoinRequestUseCase(
    private val joinRequests: JoinRequestRepository,
    private val members: UserRepository,
    private val maxRequests: Int,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(id: TelegramUserId): Result {
        if (!joinRequests.hasAnyFrom(id))
            return Result.NotSentAny

        joinRequests.removeFrom(id)
        return Result.Success
    }

    sealed interface Result {
        data object NotSentAny : Result
        data object Success : Result
    }
}
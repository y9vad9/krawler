package com.y9vad9.bcm.core.system.usecase.join_request

import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.core.system.repository.JoinRequestRepository
import kotlin.uuid.ExperimentalUuidApi

class RevokeJoinRequestUseCase(
    private val joinRequests: JoinRequestRepository,
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
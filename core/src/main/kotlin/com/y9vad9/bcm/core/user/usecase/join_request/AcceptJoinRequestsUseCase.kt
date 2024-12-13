package com.y9vad9.bcm.core.user.usecase.join_request

import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.user.repository.JoinRequestRepository
import com.y9vad9.bcm.core.user.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AcceptJoinRequestsUseCase(
    private val userRepository: UserRepository,
    private val joinRequests: JoinRequestRepository,
    private val settingsRepository: SettingsRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(
        id: TelegramUserId,
        requestId: Uuid,
        customMessage: CustomMessage,
    ): Result {
        userRepository.getById(id).getOrElse { return Result.Failure(it) }
        val request = joinRequests.getRequest(requestId) ?: return Result.NotFound
        val settings = settingsRepository.getSettings()

        val hasPermission = settings.allowedClubs[request.clubTag]?.admins?.let {
            id in it
        } == true || id in settings.admins

        if (!hasPermission) return Result.AccessDenied

        joinRequests.accept(requestId, customMessage)
        return Result.Success
    }

    sealed interface Result {
        data object AccessDenied : Result
        data object Success : Result
        data object NotFound : Result
        data class Failure(val throwable: Throwable) : Result
    }
}
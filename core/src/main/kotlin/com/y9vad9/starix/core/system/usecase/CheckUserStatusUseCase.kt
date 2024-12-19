package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.core.system.entity.User
import com.y9vad9.starix.core.system.repository.UserRepository

class CheckUserStatusUseCase(
    private val users: UserRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(userId: TelegramUserId): Result {
        try {
            val isInSystem = users.isInSystem(userId)
                .getOrElse { return Result.Failure(it) }
            if (!isInSystem) return Result.Guest

            val settings = settingsRepository.getSettings()
            val user = users.getById(userId).getOrElse { return Result.Guest }

            return when {
                user.telegramAccount?.id in settings.admins -> Result.Admin(user)
                user.bsPlayers.orEmpty().any() -> Result.Member(user)
                else -> Result.Guest
            }
        } catch (e: Throwable) {
            return Result.Failure(e)
        }
    }

    sealed interface Result {
        data class Admin(val user: User) : Result
        data class Member(val user: User) : Result
        data object Guest : Result
        data class Failure(val throwable: Throwable) : Result
    }
}
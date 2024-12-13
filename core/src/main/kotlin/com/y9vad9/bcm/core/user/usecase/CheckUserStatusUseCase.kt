package com.y9vad9.bcm.core.user.usecase

import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.user.entity.User
import com.y9vad9.bcm.core.user.repository.UserRepository

class CheckUserStatusUseCase(
    private val users: UserRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(userId: TelegramUserId): Result {
        val settings = settingsRepository.getSettings()
        val user = users.getById(userId).getOrElse { return Result.Guest }

        return when {
            user.telegramAccount?.id in settings.admins -> Result.Admin(user)
            user.bsPlayers.orEmpty().any() -> Result.Member(user)
            else -> Result.Guest
        }
    }

    sealed interface Result {
        data class Admin(val user: User) : Result
        data class Member(val user: User) : Result
        data object Guest : Result
    }
}
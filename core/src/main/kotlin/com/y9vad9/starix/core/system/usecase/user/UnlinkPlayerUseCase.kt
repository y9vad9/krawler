package com.y9vad9.starix.core.system.usecase.user

import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.entity.getPlayerOrNull
import com.y9vad9.bcm.core.system.entity.isAdminIn
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.system.repository.UserRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId

class UnlinkPlayerUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(
        initializerId: TelegramUserId,
        tag: PlayerTag,
    ): Result {
        val user = userRepository.getByTag(tag)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.NotLinked

        val player = user.getPlayerOrNull(tag) ?: return Result.NotLinked

        if (player.club == null)
            return Result.NotLinked

        val settings = settingsRepository.getSettings()

        if (user.telegramAccount?.id != initializerId && !settings.isAdminIn(player.club.tag!!, initializerId))
            return Result.NoAccess

        userRepository.unlink(tag).onFailure {
            return Result.Failure(it)
        }
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NoAccess : Result
        data object NotLinked : Result
        data class Failure(val throwable: Throwable) : Result
    }
}
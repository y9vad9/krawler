package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.entity.getPlayerOrNull
import com.y9vad9.bcm.core.system.entity.isAdminIn
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.system.repository.UserRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId

class LinkPlayerUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val timeProvider: TimeProvider,
) {

    suspend fun execute(
        initializerId: TelegramUserId,
        playerTag: PlayerTag,
        userId: TelegramUserId,
    ): Result {
        val settings = settingsRepository.getSettings()
        val user = userRepository.getByTag(playerTag)
            .getOrElse { return Result.Failure(it) }
        val player = user?.getPlayerOrNull(playerTag)

        if (user == null)
            return Result.ShouldUnlinkFirst

        if(player?.club == null)
            return Result.PlayerNotInTheClub

        if(!settings.isAdminIn(player.club.tag!!, initializerId))
            return Result.NoPermission

        userRepository.link(playerTag, userId, timeProvider.provide()).onFailure {
            return Result.Failure(it)
        }
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object NoPermission : Result
        data object ShouldUnlinkFirst : Result
        data object PlayerNotInTheClub : Result
        data class Failure(val error: Throwable) : Result
    }
}
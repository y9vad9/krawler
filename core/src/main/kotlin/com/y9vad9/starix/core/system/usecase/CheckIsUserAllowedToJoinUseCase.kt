package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.bcm.core.system.repository.UserRepository

class CheckIsUserAllowedToJoinUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(id: TelegramUserId, groupId: TelegramGroupId): Result {
        val user = userRepository.getById(id)
            .getOrElse { return Result.Failure(it) }

        val allowedClubs = settingsRepository.getSettings().allowedClubs
        val rightPlayer = user.bsPlayers?.firstOrNull { it.club?.tag in allowedClubs } ?: return Result.Denied

        if (allowedClubs[rightPlayer.club!!.tag!!]!!.linkedTelegramChat != groupId)
            return Result.Denied

        return Result.Allowed(rightPlayer)
    }

    sealed interface Result {
        data class Failure(val throwable: Throwable) : Result
        data class Allowed(val player: com.y9vad9.starix.core.brawlstars.entity.player.Player) : Result
        data object Denied : Result
    }
}
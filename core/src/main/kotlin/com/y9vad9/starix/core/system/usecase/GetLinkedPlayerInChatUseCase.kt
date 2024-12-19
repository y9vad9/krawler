package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.core.system.repository.UserRepository

class GetLinkedPlayerInChatUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(id: TelegramUserId, groupId: TelegramGroupId): Result {
        val user = userRepository.getById(id)
            .getOrElse { return Result.Failure(it) }

        val allowedClubs = settingsRepository.getSettings().allowedClubs
        val rightPlayer = user.bsPlayers?.firstOrNull {
            it.club?.tag in allowedClubs && allowedClubs[it.club!!.tag]?.linkedTelegramChat == groupId
        } ?: return Result.NotFound

        return Result.Success(rightPlayer)
    }

    sealed interface Result {
        data class Failure(val throwable: Throwable) : Result
        data class Success(val player: com.y9vad9.starix.core.brawlstars.entity.player.Player) : Result
        data object NotFound : Result
    }
}
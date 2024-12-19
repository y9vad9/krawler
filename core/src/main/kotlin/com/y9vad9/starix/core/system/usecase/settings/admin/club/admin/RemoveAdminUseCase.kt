package com.y9vad9.starix.core.system.usecase.settings.admin.club.admin

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.system.repository.UserRepository

class RemoveAdminUseCase(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) {
    suspend fun execute(clubTag: ClubTag, playerTag: PlayerTag): Result {
        val user = userRepository.getByTag(playerTag)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.UserNotLinkedAccount

        val clubsSettings = settingsRepository.getSettings().allowedClubs.toMutableMap().apply {
            val clubSettings = get(clubTag)!!
            put(
                key = clubTag,
                value = clubSettings.copy(admins = clubSettings.admins.filter { it != user.telegramAccount?.id }),
            )
        }

        settingsRepository.setSettings(settingsRepository.getSettings().copy(allowedClubs = clubsSettings))
        return Result.Success
    }

    sealed interface Result {
        data object UserNotLinkedAccount : Result
        data object Success : Result
        data class Failure(val error: Throwable) : Result
    }
}
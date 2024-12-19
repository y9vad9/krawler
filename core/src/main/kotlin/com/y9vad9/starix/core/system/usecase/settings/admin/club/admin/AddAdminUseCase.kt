package com.y9vad9.starix.core.system.usecase.settings.admin.club.admin

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.system.repository.UserRepository

class AddAdminUseCase(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) {
    suspend fun execute(clubTag: ClubTag, playerTag: PlayerTag): Result {
        val user = userRepository.getByTag(playerTag)
            .getOrElse { return Result.Failure(it) }

        val clubsSettings = settingsRepository.getSettings().allowedClubs.toMutableMap().apply {
            val clubSettings = get(clubTag)!!
            put(
                key = clubTag,
                value = clubSettings.copy(
                    admins = clubSettings.admins.toMutableList().apply {
                        add(user?.telegramAccount?.id ?: return Result.UserNotLinkedAccount)
                    }
                ),
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
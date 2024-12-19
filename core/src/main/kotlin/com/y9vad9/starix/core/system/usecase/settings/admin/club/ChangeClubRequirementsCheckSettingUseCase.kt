@file:Suppress("DuplicatedCode")

package com.y9vad9.starix.core.system.usecase.settings.admin.club

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.entity.isAdminIn
import com.y9vad9.bcm.core.system.entity.isClubAllowed
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId

class ChangeClubRequirementsCheckSettingUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(
        id: TelegramUserId,
        clubTag: ClubTag,
        status: Boolean,
    ): Result {
        val settings = settingsRepository.getSettings()
        if (!settings.isAdminIn(clubTag, id))
            return Result.NoPermission

        if (!settings.isClubAllowed(clubTag))
            return Result.ClubNotFound

        settingsRepository.setSettings(
            settings.copy(
                allowedClubs = settings.allowedClubs.toMutableMap().apply {
                    put(clubTag, settings.allowedClubs[clubTag]!!.copy(joinWithoutRequirementsCheck = status))
                }
            )
        )

        return Result.Success
    }

    sealed interface Result {
        data object ClubNotFound : Result
        data object NoPermission : Result
        data object Success : Result
    }
}
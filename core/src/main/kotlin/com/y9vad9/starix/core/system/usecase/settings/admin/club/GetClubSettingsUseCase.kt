package com.y9vad9.starix.core.system.usecase.settings.admin.club

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.entity.ClubSettings
import com.y9vad9.starix.core.system.repository.SettingsRepository

class GetClubSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(clubTag: ClubTag): Result {
        return Result.Success(
            settingsRepository.getSettings().allowedClubs[clubTag]
                ?: return Result.ClubNotFound
        )
    }

    sealed interface Result {
        data class Success(val clubSettings: ClubSettings) : Result
        data object ClubNotFound : Result
    }
}
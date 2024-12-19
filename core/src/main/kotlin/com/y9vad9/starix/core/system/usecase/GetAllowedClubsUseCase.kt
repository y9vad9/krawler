package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.system.repository.SettingsRepository

class GetAllowedClubsUseCase(
    private val brawlStarsRepository: BrawlStarsRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute(): Result {
        val allowedClubs = settingsRepository.getSettings()
            .allowedClubs.keys
            .mapNotNull {
                brawlStarsRepository.getClub(it).getOrElse { return Result.Failure(it) }
            }

        return Result.Success(allowedClubs)
    }

    sealed class Result {
        data class Success(val list: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>) : Result()
        data class Failure(val error: Throwable) : Result()
    }
}
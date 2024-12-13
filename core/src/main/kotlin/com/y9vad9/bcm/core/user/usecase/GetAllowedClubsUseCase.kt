package com.y9vad9.bcm.core.user.usecase

import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.system.repository.SettingsRepository

class GetAllowedClubsUseCase(
    private val settingsRepository: SettingsRepository,
    private val brawlStarsRepository: BrawlStarsRepository,
) {
    suspend fun execute(): Result {
        return try {
            Result.Success(settingsRepository.getSettings().allowedClubs.keys.map { brawlStarsRepository.getClub(it).getOrThrow()!! })
        } catch (t: Throwable) {
            Result.Failure(t)
        }
    }

    sealed interface Result {
        data class Success(val clubs: List<Club>) : Result
        data class Failure(val error: Throwable) : Result
    }
}
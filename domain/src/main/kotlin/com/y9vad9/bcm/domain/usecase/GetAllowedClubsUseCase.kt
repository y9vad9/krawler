package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.SettingsRepository

class GetAllowedClubsUseCase(
    private val settingsRepository: SettingsRepository,
    private val brawlStarsRepository: BrawlStarsRepository,
) {
    suspend fun execute(): Result {

    }

    sealed interface Result {
        data class Success(val clubs: List<BrawlStarsClub>) : Result
        data class Failure(val error: Throwable) : Result
    }
}
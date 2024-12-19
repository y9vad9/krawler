package com.y9vad9.starix.core.system.usecase.settings.admin.club.admin

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.system.repository.UserRepository

class GetAdminsListUseCase(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) {
    suspend fun execute(clubTag: ClubTag): Result {
        val players = settingsRepository.getSettings()
            .allowedClubs[clubTag]!!
            .admins
            .mapNotNull {
                userRepository.getById(it).getOrElse { return Result.Failure(it) }
                    .bsPlayers?.firstOrNull { it.club?.tag == clubTag }
            }

        return Result.Success(players)
    }

    sealed interface Result {
        data class Success(val players: List<com.y9vad9.starix.core.brawlstars.entity.player.Player>) : Result
        data class Failure(val error: Throwable) : Result
    }
}
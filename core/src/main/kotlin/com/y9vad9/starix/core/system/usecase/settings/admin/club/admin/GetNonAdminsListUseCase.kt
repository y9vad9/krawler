package com.y9vad9.starix.core.system.usecase.settings.admin.club.admin

import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.system.repository.UserRepository

class GetNonAdminsListUseCase(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val brawlStarsRepository: BrawlStarsRepository,
) {
    suspend fun execute(clubTag: ClubTag): Result {
        val admins = settingsRepository.getSettings()
            .allowedClubs[clubTag]!!
            .admins
            .mapNotNull {
                userRepository.getById(it).getOrElse { return Result.Failure(it) }
                    .bsPlayers?.firstOrNull { it.club?.tag == clubTag }
                    ?.tag
            }
        val members = brawlStarsRepository.getClub(clubTag)
            .getOrElse { return Result.Failure(it) }
            ?.members ?: return Result.ClubNotFound

        return Result.Success(
            members.filterNot { it.tag in admins }
        )
    }

    sealed interface Result {
        data object ClubNotFound : Result
        data class Success(val players: List<ClubMember>) : Result
        data class Failure(val error: Throwable) : Result
    }
}
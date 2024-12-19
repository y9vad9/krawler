package com.y9vad9.starix.core.system.usecase

import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.starix.core.system.repository.UserRepository

class GetNotLinkedBrawlStarsPlayersUseCase(
    private val brawlStarsRepository: BrawlStarsRepository,
    private val userRepository: UserRepository,
) {

    suspend fun execute(clubTag: ClubTag): Result {
        val players = brawlStarsRepository.getClub(clubTag)
            .getOrElse { return Result.Failure(it) }
            ?.members ?: return Result.ClubNotFound

        val nonLinked: List<ClubMember> = players.filter {
            !userRepository.isInSystem(it.tag).getOrElse { return Result.Failure(it) }
        }

        return Result.Success(nonLinked)
    }

    sealed class Result {
        data object ClubNotFound : Result()
        data class Success(val list: List<ClubMember>) : Result()
        data class Failure(val error: Throwable) : Result()
    }
}
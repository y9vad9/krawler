package com.y9vad9.starix.core.brawlstars.usecase

import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.starix.core.system.repository.UserRepository

class GetPlayersInClubWithLinkageUseCase(
    private val bsRepository: BrawlStarsRepository,
    private val userRepository: UserRepository,
) {
    suspend fun execute(clubTag: ClubTag): Result {
        val club = bsRepository.getClub(clubTag)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.ClubNotFound

        val members = club.members.associateWith {
            userRepository.isInSystem(it.tag).getOrElse { return Result.Failure(it) }
        }

        return Result.Success(members)
    }

    sealed interface Result {
        data class Failure(val throwable: Throwable) : Result

        /**
         * @param members map of club members and the boolean that represents whether user
         * is linked or not.
         */
        data class Success(val members: Map<ClubMember, Boolean>) : Result
        data object ClubNotFound : Result
    }
}
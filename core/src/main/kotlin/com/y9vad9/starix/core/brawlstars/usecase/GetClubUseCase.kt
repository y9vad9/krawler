package com.y9vad9.starix.core.brawlstars.usecase

import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository

class GetClubUseCase(
    private val bsRepository: BrawlStarsRepository,
) {
    suspend fun execute(clubTag: ClubTag): Result {
        val club = bsRepository.getClub(clubTag)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.ClubNotFound

        return Result.Success(club)
    }

    sealed interface Result {
        data class Failure(val throwable: Throwable) : Result

        /**
         * @param members map of club members and the boolean that represents whether user
         * is linked or not.
         */
        data class Success(val club: Club) : Result
        data object ClubNotFound : Result
    }
}
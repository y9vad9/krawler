package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.User
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.UserRepository

class CheckIsInTheClubUseCase(
    private val brawlStars: BrawlStarsRepository,
    private val users: UserRepository,
) {
    suspend fun execute() {}

    sealed interface Result {
        data class Failure(val error: Throwable) : Result
        data class InClub(
            val player: BrawlStarsPlayer,
            val club: Club,
        ) : Result
    }
}
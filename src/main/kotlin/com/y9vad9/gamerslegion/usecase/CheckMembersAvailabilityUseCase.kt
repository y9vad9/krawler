package com.y9vad9.gamerslegion.usecase

import com.y9vad9.gamerslegion.brawlstars.BrawlStarsClient
import com.y9vad9.gamerslegion.database.MembersRepository

class CheckMembersAvailabilityUseCase(
    private val bsClient: BrawlStarsClient,
    private val membersRepository: MembersRepository,
    private val clubTag: String = "#2YJ2RGGVC",
) {
    suspend fun execute(): Result {
        val members = try {
            bsClient.getClubMembers(clubTag)
        } catch (e: Exception) {
            return Result.ApiFailure(e)
        }

        return if (members.size == 30)
            Result.CannotJoin
        else Result.CanJoin(30 - members.size)
    }

    sealed interface Result {
        data class CanJoin(val availableSeats: Int) : Result
        data object CannotJoin : Result
        data class ApiFailure(val exception: Exception) : Result
    }
}
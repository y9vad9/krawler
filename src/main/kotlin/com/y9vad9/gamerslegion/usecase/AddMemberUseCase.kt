package com.y9vad9.gamerslegion.usecase

import com.y9vad9.gamerslegion.brawlstars.BrawlStarsClient
import com.y9vad9.gamerslegion.brawlstars.entity.Member
import com.y9vad9.gamerslegion.database.MembersRepository

class AddMemberUseCase(
    private val bsClient: BrawlStarsClient,
    private val membersRepository: MembersRepository,
    private val clubTag: String = "#2YJ2RGGVC"
) {
    suspend fun execute(
        tgId: Long,
        tag: String,
    ): Result {
        val members = try {
            bsClient.getClubMembers(clubTag)
        } catch (e: Exception) {
            return Result.ApiFailure(e)
        }

        val member = members.firstOrNull { member ->
            member.tag == tag
        }

        if (member == null) {
            return if (members.size == 30)
                Result.NotInTheClub.CannotJoin
            else Result.NotInTheClub.ButCanJoin(30 - members.size)
        }

        if(membersRepository.getByTag(tag) != null)
            return Result.AlreadyIn

        membersRepository.addMember(tgId, member.name, member.tag)
        return Result.Success(member)
    }

    sealed interface Result {
        data class Success(val member: Member) : Result
        data object InvalidTag : Result
        data object AlreadyIn : Result
        sealed interface NotInTheClub : Result {
            data class ButCanJoin(val availableSeats: Int) : Result
            data object CannotJoin : Result
        }
        data class ApiFailure(val exception: Exception) : Result
    }
}
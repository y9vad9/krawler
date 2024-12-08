package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.User
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.telegram.LinkedTelegramAccount
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.repository.UserRepository

class AddMemberUseCase(
    private val userRepository: UserRepository,
    private val clubRepository: ClubRepository,
) {
    suspend fun execute(
        tag: PlayerTag,
        id: TelegramUserId,
    ): Result {
        val member = userRepository.getByTag(tag) ?: return Result.InvalidTag
        val allowedClubs = clubRepository.getAllowedClubs()
        val allowedClubsTag = allowedClubs.map { it.bs.tag }

        if (member.bsPlayers!!.club.tag !in allowedClubsTag) {
            val availableClubs = allowedClubs.filter { club ->
                club.canJoin
            }
        }

        if (member.bsPlayers!!.club.tag in allowedClubs) {
            if (member.isTelegramLinked)
                userRepository.link(tag, LinkedTelegramAccount(id))
        }
    }

    sealed interface Result {
        data class Success(val user: User) : Result
        data object InvalidTag : Result
        data object AlreadyIn : Result
        sealed interface NotInTheClub : Result {
            data class ButCanJoin(val available: List<BrawlStarsClub>) : Result
            data object CannotJoin : Result
        }
        data class ApiFailure(val exception: Exception) : Result
    }
}
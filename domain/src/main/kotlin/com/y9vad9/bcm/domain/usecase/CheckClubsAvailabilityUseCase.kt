package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubType
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.repository.UserRepository

class CheckClubsAvailabilityUseCase(
    private val userRepository: UserRepository,
    private val clubRepository: ClubRepository,
) {
    suspend fun execute(
        tag: PlayerTag,
    ): Result = try {
        val member = userRepository.getByTag(tag) ?: return Result.NoPlayerFound
        val player = member.bsPlayers!!.first { player -> player.tag.toString() == tag.toString() }
        val allowedClubs = clubRepository.getAllowedClubs()

        Result.Success(
            allowedClubs.map { club ->
                when {
                    player.trophies < club.bs.requiredTrophies ->
                        Result.ClubState.NotEnoughTrophies(club)

                    club.bs.type == ClubType.OPEN -> Result.ClubState.Open(club)

                    club.acceptsRequests -> Result.ClubState.UponRequest(club)

                    club.bs.type == ClubType.INVITE_ONLY -> Result.ClubState.OnlyInvite(club)

                    else -> Result.ClubState.NotAvailable(club)
                }
            })
    } catch (e: Exception) {
        Result.Failure(e)
    }

    sealed interface Result {
        data object NoPlayerFound : Result
        data class Failure(val exception: Exception) : Result
        data class Success(val states: List<ClubState>) : Result

        sealed interface ClubState {
            val club: Club

            data class NotEnoughTrophies(override val club: Club) : ClubState

            /**
             * The system of clubs/club has no available seats at all.
             */
            data class NotAvailable(override val club: Club) : ClubState

            /**
             * The system of clubs/club has only invite mode, but with available seats.
             */
            data class OnlyInvite(override val club: Club) : ClubState

            /**
             * Club accepts join requests via bot.
             */
            data class UponRequest(override val club: Club) : ClubState

            /**
             * The System of clubs/club has open mode and free seats.
             */
            data class Open(override val club: Club) : ClubState
        }
    }
}
package com.y9vad9.bcm.core.brawlstars.usecase

import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubType
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.user.entity.ClubJoinAbility
import com.y9vad9.bcm.core.user.repository.UserRepository

class CheckClubsAvailabilityUseCase(
    private val users: UserRepository,
    private val brawlStars: BrawlStarsRepository,
    private val settings: SettingsRepository,
) {
    suspend fun execute(
        tag: PlayerTag,
    ): Result {
        return try {
            val player = brawlStars.getPlayer(tag)
                .getOrElse { exception -> return Result.Failure(exception) }
                ?: return Result.NoPlayerFound
            users.getByTag(tag)
                .getOrElse { exception -> Result.Failure(exception) }
                ?: return Result.NoPlayerFound

            val clubsSettings = settings.getSettings().allowedClubs
            val allowedClubs = settings.getSettings().allowedClubs.map { (tag, _) ->
                brawlStars.getClub(tag)
                    .getOrElse { exception -> return Result.Failure(exception) }!! // should blow if club is not found
            }

            Result.Success(
                allowedClubs.map { club ->
                    val settings = clubsSettings[club.tag]!!
                    when {
                        player.trophies < club.requiredTrophies -> {
                            if (settings.joinViaBotRequest && settings.joinWithoutRequirementsCheck)
                                ClubJoinAbility.UponRequest(club)
                            else ClubJoinAbility.NotEnoughTrophies(club, club.requiredTrophies - player.trophies)
                        }

                        club.type == ClubType.OPEN -> ClubJoinAbility.Open(club)

                        settings.joinViaBotRequest -> ClubJoinAbility.UponRequest(club)

                        club.type == ClubType.INVITE_ONLY -> ClubJoinAbility.OnlyInvite(club)

                        else -> ClubJoinAbility.NotAvailable(club)
                    }
                })
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    sealed interface Result {
        data object NoPlayerFound : Result
        data class Failure(val error: Throwable) : Result
        data class Success(val abilities: List<ClubJoinAbility>) : Result
    }
}
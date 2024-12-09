package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubType
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.SettingsRepository
import com.y9vad9.bcm.domain.repository.UserRepository

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
                    val fullClub = Club(club, clubsSettings[club.tag]!!)
                    when {
                        player.trophies < club.requiredTrophies -> {
                            if (fullClub.settings.joinViaBotRequest && fullClub.settings.joinWithoutRequirementsCheck)
                                ClubJoinAbility.UponRequest(fullClub)
                            else ClubJoinAbility.NotEnoughTrophies(fullClub)
                        }

                        club.type == ClubType.OPEN -> ClubJoinAbility.Open(fullClub)

                        fullClub.settings.joinViaBotRequest -> ClubJoinAbility.UponRequest(fullClub)

                        club.type == ClubType.INVITE_ONLY -> ClubJoinAbility.OnlyInvite(fullClub)

                        else -> ClubJoinAbility.NotAvailable(fullClub)
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
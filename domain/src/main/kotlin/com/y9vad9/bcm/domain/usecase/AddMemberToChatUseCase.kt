package com.y9vad9.bcm.domain.usecase

import com.timemates.backend.time.TimeProvider
import com.y9vad9.bcm.domain.entity.ClubJoinAbility
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.getPlayerOrNull
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.entity.value.Link
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.ChatRepository
import com.y9vad9.bcm.domain.repository.SettingsRepository
import com.y9vad9.bcm.domain.repository.UserRepository

class AddMemberToChatUseCase(
    private val users: UserRepository,
    private val brawlStars: BrawlStarsRepository,
    private val settingsRepository: SettingsRepository,
    private val chatRepository: ChatRepository,
    private val time: TimeProvider,
    private val checkClubsAvailability: CheckClubsAvailabilityUseCase,
) {
    suspend fun execute(
        tag: PlayerTag,
        id: TelegramUserId,
    ): Result {
        val user = users.getByTag(tag)
            .getOrElse { exception -> return Result.Failure(exception) }
        val settings = settingsRepository.getSettings()

        val bsPlayer = brawlStars.getPlayer(tag = tag, withInvalidate = true)
            .getOrElse { exception -> return Result.Failure(exception) }
            ?: return Result.PlayerNotFound

        if (user != null) {
            val shouldProvideLink = id == user.telegramAccount?.id && bsPlayer.club?.tag in settings.allowedClubs.keys &&
                user.getPlayerOrNull(tag)!!.club?.tag?.let {
                    settings.allowedClubs[it]!!.linkedTelegramChat
                }?.let { chatRepository.isMemberOfGroup(it, id) } == false

            return Result.AlreadyIn(
                shouldProvideChatLink = shouldProvideLink,
                link = if (shouldProvideLink) chatRepository.createInviteLink(settings.allowedClubs[player!!.club!!.tag]!!.linkedTelegramChat) else null,
                player = bsPlayer,
                clubTag = bsPlayer.club?.tag
            )
        }



        val playerClubTag = bsPlayer.club?.tag

        if (playerClubTag != null) {
            val allowedClubs = settings.allowedClubs

            if (allowedClubs.containsKey(playerClubTag)) {
                val link = chatRepository.createInviteLink(
                    groupId = allowedClubs[playerClubTag]!!.linkedTelegramChat
                )
                users.link(tag, id, time.provide())
                    .onFailure { exception -> return Result.Failure(exception) }

                return Result.Success(
                    player = bsPlayer,
                    inviteLink = link,
                    clubTag = playerClubTag
                )
            }
        }

        return when (val result = checkClubsAvailability.execute(tag)) {
            is CheckClubsAvailabilityUseCase.Result.Failure -> Result.Failure(result.error)
            CheckClubsAvailabilityUseCase.Result.NoPlayerFound -> Result.PlayerNotFound
            is CheckClubsAvailabilityUseCase.Result.Success -> Result.NotInTheClub(result.abilities)
        }
    }

    sealed interface Result {
        data class Success(
            val player: BrawlStarsPlayer,
            val clubTag: ClubTag,
            val inviteLink: Link,
        ) : Result

        data object PlayerNotFound : Result
        data class AlreadyIn(
            val player: BrawlStarsPlayer,
            val shouldProvideChatLink: Boolean,
            val link: Link?,
            val clubTag: ClubTag?,
        ) : Result
        data class NotInTheClub(val clubs: List<ClubJoinAbility>) : Result

        data class Failure(val throwable: Throwable) : Result
    }
}
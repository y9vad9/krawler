package com.y9vad9.starix.core.telegram.usecase

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.starix.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.starix.core.common.entity.value.Link
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.core.telegram.repository.ChatRepository
import com.y9vad9.starix.core.system.entity.ClubJoinAbility
import com.y9vad9.starix.core.system.repository.UserRepository

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

        val player = brawlStars.getPlayer(tag = tag, withInvalidate = true)
            .getOrElse { exception -> return Result.Failure(exception) }
            ?: return Result.PlayerNotFound

        if (user != null) {
            val shouldProvideChatLink = when {
                id != user.telegramAccount?.id -> false
                player.club == null || player.club.tag !in settings.allowedClubs -> false
                !settings.allowedClubs.containsKey(player.club.tag) -> false
                else -> {
                    val groupId = settings.allowedClubs[player.club.tag]!!.linkedTelegramChat
                    !chatRepository.isMemberOfGroup(groupId!!, id)
                }
            }

            return Result.Success(
                shouldProvideChatLink = shouldProvideChatLink,
                link = if (shouldProvideChatLink) chatRepository.createInviteLink(settings.allowedClubs[player.club!!.tag]!!.linkedTelegramChat) else null,
                player = player,
                club = player.club!!
            )
        }

        return when (val result = checkClubsAvailability.execute(tag)) {
            is CheckClubsAvailabilityUseCase.Result.Failure -> Result.Failure(result.error)
            CheckClubsAvailabilityUseCase.Result.NoPlayerFound -> Result.PlayerNotFound
            is CheckClubsAvailabilityUseCase.Result.Success -> Result.NotInTheClub(result.abilities)
        }
    }

    sealed interface Result {

        data object PlayerNotFound : Result
        data class Success(
            val player: com.y9vad9.starix.core.brawlstars.entity.player.Player,
            val shouldProvideChatLink: Boolean,
            val link: Link?,
            val club: _root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club.View,
        ) : Result

        data class NotInTheClub(val clubs: List<ClubJoinAbility>) : Result

        data class Failure(val throwable: Throwable) : Result
    }
}
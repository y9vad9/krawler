package com.y9vad9.starix.core.brawlstars.usecase.excused

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.bcm.core.system.entity.isAdminIn
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId

class GetListOfExcusedPlayersUseCase(
    private val settingsRepository: SettingsRepository,
    private val brawlStarsRepository: BrawlStarsRepository,
    private val timeProvider: TimeProvider,
) {
    suspend fun execute(
        id: TelegramUserId,
        clubTag: ClubTag,
    ): Result {
        if (!settingsRepository.getSettings().isAdminIn(clubTag, id))
            return Result.NoPermission

        val players = brawlStarsRepository.getExcusedList(clubTag, timeProvider.provide())
            .getOrElse { return Result.Failure(it) }

        return Result.Success(players)
    }

    sealed interface Result {
        data class Success(val players: Map<ClubMember, UnixTime>) : Result
        data object NoPermission : Result
        data class Failure(val error: Throwable) : Result
    }
}
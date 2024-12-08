package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.repository.UserRepository

class CheckLinkedBrawlStarsAccountsUseCase(
    private val members: UserRepository,
) {
    suspend fun execute(id: TelegramUserId): Result {
        val member = members.getById(id)

        return when(member.bsPlayers?.size) {
            null -> Result.None
            1 -> Result.Single(member.bsPlayers.first())
            else -> Result.Multiple(member.bsPlayers)
        }
    }

    sealed interface Result {
        data object None : Result
        data class Single(val player: BrawlStarsPlayer) : Result
        data class Multiple(val players: List<BrawlStarsPlayer>) : Result
    }
}
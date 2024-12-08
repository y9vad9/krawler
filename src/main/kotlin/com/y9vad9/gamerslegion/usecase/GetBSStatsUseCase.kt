package com.y9vad9.gamerslegion.usecase

import com.y9vad9.gamerslegion.brawlstars.BrawlStarsClient
import com.y9vad9.gamerslegion.brawlstars.entity.Player

class GetBSStatsUseCase(
    private val bsClient: BrawlStarsClient
) {
    suspend fun execute(tag: String): Result {
        return try {
            Result.Success(bsClient.getPlayer(tag))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    sealed class Result {
        data class Success(val player: Player) : Result()
        data class Failure(val exception: Exception) : Result()
    }
}
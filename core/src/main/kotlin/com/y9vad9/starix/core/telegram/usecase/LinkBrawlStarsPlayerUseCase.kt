package com.y9vad9.starix.core.telegram.usecase

import com.y9vad9.starix.foundation.time.TimeProvider
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.brawlstars.repository.BrawlStarsRepository
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.core.system.entity.getPlayerOrNull
import com.y9vad9.starix.core.system.repository.UserRepository

class LinkBrawlStarsPlayerUseCase(
    private val users: UserRepository,
    private val brawlStars: BrawlStarsRepository,
    private val time: TimeProvider,
) {
    suspend fun execute(userId: TelegramUserId, tag: PlayerTag): Result {
        val user = users.getById(userId)
            .getOrElse { exception -> return Result.Failure(exception) }

        if (user.bsPlayers?.any { player -> player.tag == tag } == true) {
            return Result.Success(user.getPlayerOrNull(tag)!!)
        }

        val possiblyLinkedUser = users.getByTag(tag)
            .getOrElse { exception -> return Result.Failure(exception) }

        if (possiblyLinkedUser != null)
            return Result.AlreadyLinked

        val player = brawlStars.getPlayer(tag)
            .getOrElse { exception -> return Result.Failure(exception) }

        if (player == null)
            return Result.PlayerDoesNotExists

        users.link(tag, userId, time.provide())
            .getOrElse { exception -> return Result.Failure(exception) }!!

        return Result.Success(player)
    }

    sealed class Result {
        data class Success(val player: com.y9vad9.starix.core.brawlstars.entity.player.Player) : Result()
        data object PlayerDoesNotExists : Result()
        data object AlreadyLinked : Result()
        data class Failure(val throwable: Throwable) : Result()
    }
}
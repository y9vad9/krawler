package com.y9vad9.bcm.domain.usecase

import com.timemates.backend.time.TimeProvider
import com.y9vad9.bcm.domain.entity.User
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsPlayer
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.entity.getPlayerOrNull
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.repository.BrawlStarsRepository
import com.y9vad9.bcm.domain.repository.UserRepository

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
            .getOrElse { exception -> Result.Failure(exception) }

        if (player == null)
            return Result.PlayerDoesNotExists

        return Result.Success(
            users.link(tag, userId, time.provide())
                .getOrElse { exception -> return Result.Failure(exception) }!!
                .getPlayerOrNull(tag)!! // safe: should be created at this point
        )
    }

    sealed class Result {
        data class Success(val user: BrawlStarsPlayer) : Result()
        data object PlayerDoesNotExists : Result()
        data object AlreadyLinked : Result()
        data class Failure(val throwable: Throwable) : Result()
    }
}
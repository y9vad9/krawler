package com.y9vad9.brawlex.user.application.usecase

import com.y9vad9.brawlex.user.application.repository.PlayerRepository
import com.y9vad9.brawlex.user.application.repository.UserRepository
import com.y9vad9.brawlex.user.domain.event.UserUpdateEvent
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.brawlex.user.domain.value.UserId

/**
 * Use case for linking a Brawl Stars player to a user.
 *
 * Checks if the user exists and if the player is already linked,
 * then verifies the player exists in the system before linking.
 *
 * Returns detailed results including success, domain errors, or failures.
 *
 * @param userRepository repository to access and update user data
 * @param playerRepository repository to verify existence of players
 */
class LinkPlayerUseCase(
    private val userRepository: UserRepository,
    private val playerRepository: PlayerRepository,
) {
    sealed interface Result {
        data class Success(val event: UserUpdateEvent.PlayerAdded) : Result
        data object AlreadyLinked : Result
        data object PlayerNotFound : Result
        data object UserNotFound : Result
        data class Failure(val throwable: Throwable) : Result
    }

    suspend fun execute(userId: UserId, tag: BrawlStarsPlayerTag): Result {
        val user = userRepository.getUserBySystemId(userId)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.UserNotFound

        if (user.linkedPlayers.has(tag))
            return Result.AlreadyLinked

        val playerResult = playerRepository.getPlayer(tag)
        val player = playerResult.getOrElse {
            return Result.Failure(it)
        } ?: return Result.PlayerNotFound

        val addResult = userRepository.addLinkedPlayer(userId, player)
        return addResult.map { Result.Success(UserUpdateEvent.PlayerAdded(userId, player)) }
            .getOrElse { Result.Failure(it) }
    }
}

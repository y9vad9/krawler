package com.y9vad9.brawlex.user.application.usecase

import com.y9vad9.brawlex.user.application.repository.UserRepository
import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.brawlex.user.domain.value.UserId

/**
 * Use case for unlinking a Brawl Stars player from a user.
 *
 * This class attempts to remove a linked player identified by [tag] from the user identified by [userId].
 * It validates user existence and linkage status before performing the removal.
 *
 * Possible [Result]s:
 * - [Result.Success]: the player was unlinked successfully and an updated [User] is returned.
 * - [Result.PlayerIsNotLinked]: the specified player was not linked to the user.
 * - [Result.UserNotFound]: no user was found with the provided ID.
 * - [Result.Failure]: an unexpected error occurred during the operation.
 */
class UnlinkPlayerUseCase(
    private val userRepository: UserRepository,
) {
    sealed interface Result {
        data class Success(val user: User) : Result
        data object PlayerIsNotLinked : Result
        data object UserNotFound : Result
        data class Failure(val throwable: Throwable) : Result
    }

    suspend fun execute(userId: UserId, tag: BrawlStarsPlayerTag): Result {
        val user: User = userRepository.getUserBySystemId(userId)
            .getOrElse { return Result.Failure(it) }
            ?: return Result.UserNotFound

        if (!user.linkedPlayers.has(tag)) {
            return Result.PlayerIsNotLinked
        }

        val (updated, event) = user.withoutLinkedPlayer(tag)

        if (event != null) {
            userRepository.removeLinkedPlayer(event.userId, event.playerTag)
                .getOrElse { throwable -> return Result.Failure(throwable) }
        }

        return Result.Success(updated)
    }
}

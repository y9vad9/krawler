package com.y9vad9.starix.core.system.usecase

import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.entity.User
import com.y9vad9.bcm.core.system.repository.UserRepository

class GetUsersByTagUseCase(
    private val userRepository: UserRepository,
) {

    suspend fun execute(tags: List<PlayerTag>): Result {
        val users = tags.map { userRepository.getByTag(it).getOrElse { return Result.Failure(it) } }
            .filterNotNull()
        return Result.Success(users)
    }

    sealed interface Result {
        data class Success(val users: List<User>) : Result
        data class Failure(val throwable: Throwable) : Result
    }
}
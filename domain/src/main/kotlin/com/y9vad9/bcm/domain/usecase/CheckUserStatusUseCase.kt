package com.y9vad9.bcm.domain.usecase

import com.y9vad9.bcm.domain.entity.User
import com.y9vad9.bcm.domain.entity.telegram.value.TelegramUserId
import com.y9vad9.bcm.domain.repository.UserRepository

class CheckUserStatusUseCase(
    private val users: UserRepository,
) {
    suspend fun execute(userId: TelegramUserId): Result {

    }

    sealed interface Result {
        data class Admin(val user: User) : Result
        data class Member(val user: User) : Result
        data object Guest : Result
    }
}
package com.y9vad9.gamerslegion.usecase

import com.y9vad9.gamerslegion.database.MemberInfo
import com.y9vad9.gamerslegion.database.MembersRepository

class CheckUserStatusUseCase(
    private val membersRepository: MembersRepository,
) {
    suspend fun execute(id: Long): Result {
        val user = membersRepository.getByTgId(id) ?: return Result.New

        return if(user.isAdmin) Result.Admin(user) else Result.Member(user)
    }

    sealed interface Result {
        data object New : Result
        data class Admin(val member: MemberInfo) : Result
        data class Member(val member: MemberInfo) : Result
    }
}
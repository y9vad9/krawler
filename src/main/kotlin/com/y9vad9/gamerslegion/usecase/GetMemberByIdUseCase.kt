package com.y9vad9.gamerslegion.usecase

import com.y9vad9.gamerslegion.database.MemberInfo
import com.y9vad9.gamerslegion.database.MembersRepository
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.chat.member.ChatMember

class GetMemberByIdUseCase(
    private val membersRepository: MembersRepository
) {
    suspend fun execute(userId: Long): Result {
        return Result.Success(membersRepository.getByTgId(userId) ?: return Result.NotFound)
    }

    sealed interface Result {
        data class Success(val member: MemberInfo) : Result
        data object NotFound : Result
    }
}
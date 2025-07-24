package com.y9vad9.brawlex.user.application.usecase

import com.y9vad9.brawlex.user.application.repository.UserRepository
import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.brawlex.user.domain.value.LinkedTelegramChatId
import com.y9vad9.brawlex.user.domain.value.LinkedTelegramUserName

/**
 * Use case responsible for resolving a user by [LinkedTelegramChatId] or registering them if absent.
 *
 * This use case serves as an entry point for Telegram-based user identification:
 * - If a user is already registered for the given [LinkedTelegramChatId], it returns [Result.AlreadyRegistered].
 * - If no user is found, it creates a new one using the provided [LinkedTelegramUserName], returning [Result.NewUser].
 * - In case of any error (retrieval or creation), it returns [Result.Failure].
 *
 * Unlike a typical authentication mechanism, this use case does not verify credentials — it assumes
 * identity based on the Telegram context and either reuses or creates a domain [User].
 *
 * @param userRepository The repository responsible for persistence and retrieval of users.
 */
class AuthenticateTelegramUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend fun execute(
        telegramChatId: LinkedTelegramChatId,
        name: LinkedTelegramUserName,
    ): Result {
        return userRepository.getUserByTelegramChatId(telegramChatId)
            .map { user ->
                if (user != null) Result.AlreadyRegistered(user)
                else userRepository.createUser(telegramChatId, name)
                    .map { Result.NewUser(it) }
                    .getOrElse { Result.Failure(it) }
            }
            .getOrElse { Result.Failure(it) }
    }

    sealed interface Result {
        /**
         * A new user was created and registered.
         */
        data class NewUser(val user: User) : Result

        /**
         * An existing user was found and reused.
         */
        data class AlreadyRegistered(val user: User) : Result

        /**
         * An error occurred during the process.
         */
        data class Failure(val throwable: Throwable) : Result
    }
}

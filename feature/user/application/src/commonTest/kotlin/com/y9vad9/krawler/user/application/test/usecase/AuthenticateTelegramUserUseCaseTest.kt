package com.y9vad9.krawler.user.application.test.usecase

import com.y9vad9.krawler.user.application.repository.UserRepository
import com.y9vad9.krawler.user.application.usecase.AuthenticateTelegramUserUseCase
import com.y9vad9.krawler.user.domain.User
import com.y9vad9.krawler.user.domain.value.LinkedTelegramChatId
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.krawler.user.domain.value.UserId
import com.y9vad9.valdi.createOrThrow
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.uuid.Uuid
import kotlinx.coroutines.test.runTest

class AuthenticateTelegramUserUseCaseTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val useCase = AuthenticateTelegramUserUseCase(userRepository)

    private val telegramChatId = LinkedTelegramChatId.factory.createOrThrow(123_456L)
    private val telegramName = LinkedTelegramUserName.factory.createOrThrow("TestUser")

    private val existingUser = User(
        id = UserId(Uuid.random()),
        linkedPlayers = mockk(relaxed = true),
        linkedTelegram = mockk(relaxed = true),
    )

    private val newUser = User(
        id = UserId(Uuid.random()),
        linkedPlayers = mockk(relaxed = true),
        linkedTelegram = mockk(relaxed = true),
    )

    @Test
    fun `execute returns AlreadyRegistered when user already exists`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserByTelegramChatId(telegramChatId) } returns Result.success(existingUser)

        // WHEN
        val result = useCase.execute(telegramChatId, telegramName)

        // THEN
        assertIs<AuthenticateTelegramUserUseCase.Result.AlreadyRegistered>(result)
        assertSame(
            actual = result.user,
            expected = existingUser,
        )
    }

    @Test
    fun `execute creates new user and returns NewUser when user does not exist`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserByTelegramChatId(telegramChatId) } returns Result.success(null)
        coEvery { userRepository.createUser(telegramChatId, telegramName) } returns Result.success(newUser)

        // WHEN
        val result = useCase.execute(telegramChatId, telegramName)

        // THEN
        assertIs<AuthenticateTelegramUserUseCase.Result.NewUser>(result)
        assertSame(
            actual = result.user,
            expected = newUser,
        )
    }

    @Test
    fun `execute returns Failure when getUserByTelegramChatId returns failure`() = runTest {
        // GIVEN
        val error = IllegalStateException("database failure")
        coEvery { userRepository.getUserByTelegramChatId(telegramChatId) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(telegramChatId, telegramName)

        // THEN
        assertIs<AuthenticateTelegramUserUseCase.Result.Failure>(result)
        assertSame(
            actual = result.throwable,
            expected = error,
        )
    }

    @Test
    fun `execute returns Failure when createUser returns failure`() = runTest {
        // GIVEN
        val error = IllegalArgumentException("cannot create user")
        coEvery { userRepository.getUserByTelegramChatId(telegramChatId) } returns Result.success(null)
        coEvery { userRepository.createUser(telegramChatId, telegramName) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(telegramChatId, telegramName)

        // THEN
        assertIs<AuthenticateTelegramUserUseCase.Result.Failure>(result)
        assertSame(
            actual = result.throwable,
            expected = error,
        )
    }
}

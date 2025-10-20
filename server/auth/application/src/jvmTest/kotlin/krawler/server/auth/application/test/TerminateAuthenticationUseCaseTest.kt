package krawler.server.auth.application.test

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.logger.Logger
import krawler.server.auth.application.AuthenticationRepository
import krawler.server.auth.application.Hasher
import krawler.server.auth.application.RefreshToken
import krawler.server.auth.application.TerminateAuthenticationUseCase
import kotlin.test.Test
import kotlin.test.assertIs

class TerminateAuthenticationUseCaseTest {

    private val authenticationRepository: AuthenticationRepository = mockk()
    private val hasher: Hasher = mockk()
    private val logger: Logger = mockk(relaxed = true)

    private val useCase = TerminateAuthenticationUseCase(
        authenticationRepository = authenticationRepository,
        hasher = hasher,
    )

    @Test
    fun `returns Success when session terminated successfully`() = runTest {
        // GIVEN
        val refreshToken = RefreshToken("valid-token")
        coEvery { authenticationRepository.terminate(refreshToken) } returns Result.success(true)

        // WHEN
        val result = useCase.execute(refreshToken, logger)

        // THEN
        assertIs<TerminateAuthenticationUseCase.Result.Success>(result)
    }

    @Test
    fun `returns NotFound when no session exists`() = runTest {
        // GIVEN
        val refreshToken = RefreshToken("nonexistent-token")
        coEvery { authenticationRepository.terminate(refreshToken) } returns Result.success(false)

        // WHEN
        val result = useCase.execute(refreshToken, logger)

        // THEN
        assertIs<TerminateAuthenticationUseCase.Result.NotFound>(result)
    }

    @Test
    fun `returns Failure and logs hashed token when repository fails`() = runTest {
        // GIVEN
        val refreshToken = RefreshToken("faulty-token")
        val throwable = RuntimeException("Repository failure")
        coEvery { authenticationRepository.terminate(refreshToken) } returns Result.failure(throwable)
        coEvery { hasher.sha512(refreshToken.string) } returns "hashed-faulty"

        // WHEN
        val result = useCase.execute(refreshToken, logger)

        // THEN
        assertIs<TerminateAuthenticationUseCase.Result.Failure>(result)
    }
}

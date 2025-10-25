@file:Suppress("RETURN_VALUE_NOT_USED")

package krawler.server.auth.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.logger.Logger
import krawler.server.auth.application.AuthenticateUseCase
import krawler.server.auth.application.AuthenticationConfig
import krawler.server.auth.application.AuthenticationSessionToken
import krawler.server.auth.application.AuthenticationSessionsRepository
import krawler.server.auth.application.OwnershipTaskGenerator
import krawler.server.auth.application.PlayerRepository
import krawler.server.auth.application.SessionTokenGenerator
import krawler.server.auth.application.TimeProvider
import krawler.server.auth.application.UuidProvider
import krawler.server.auth.domain.ChallengedBrawlStarsPlayerTag
import krawler.server.auth.domain.OwnershipTask
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class AuthenticateUseCaseTest {

    private val playerRepository: PlayerRepository = mockk()
    private val sessionsRepository: AuthenticationSessionsRepository = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val sessionTokenGenerator: SessionTokenGenerator = mockk()
    private val ownershipTaskGenerator: OwnershipTaskGenerator = mockk()
    private val uuidProvider: UuidProvider = mockk()
    private val config = AuthenticationConfig(
        activeOrFailedSessionsThreshold = 3,
        activeOrFailedSessionsPeriod = 10.minutes,
        sessionDuration = 10.minutes,
        attemptsPerSession = 3
    )
    private val logger = mockk<Logger>(relaxed = true)

    private val useCase = AuthenticateUseCase(
        playerRepository = playerRepository,
        sessionsRepository = sessionsRepository,
        timeProvider = timeProvider,
        sessionTokenGenerator = sessionTokenGenerator,
        ownershipTaskGenerator = ownershipTaskGenerator,
        uuidProvider = uuidProvider,
        config = config
    )

    private val tag = ChallengedBrawlStarsPlayerTag.createOrThrow("#ABC123")
    private val now = kotlin.time.Clock.System.now()
    private val token = AuthenticationSessionToken("token")
    private val ownershipTask = mockk<OwnershipTask>()

    @Test
    fun `returns PlayerDoesNotExist when player does not exist`() = runTest {
        // GIVEN
        coEvery { playerRepository.exists(tag = tag) } returns Result.success(false)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.PlayerDoesNotExist>(result)
    }

    @Test
    fun `returns TooManyAttempts when active or failed sessions exceed threshold`() = runTest {
        // GIVEN
        coEvery { playerRepository.exists(tag = tag) } returns Result.success(true)
        coEvery { timeProvider.provide() } returns now
        coEvery {
            sessionsRepository.getSessionsCount(
                timeline = any(),
                includeSuccessful = false,
                includeFailed = true
            )
        } returns Result.success(config.activeOrFailedSessionsThreshold)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.TooManyAttempts>(result)
    }

    @Test
    fun `returns Failure when playerRepository throws`() = runTest {
        // GIVEN
        val error = RuntimeException("repo error")
        coEvery { playerRepository.exists(tag = tag) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.Failure>(result)
    }

    @Test
    fun `returns Failure when sessionsRepository getSessionsCount throws`() = runTest {
        // GIVEN
        val error = RuntimeException("sessions repo error")
        coEvery { playerRepository.exists(tag = tag) } returns Result.success(true)
        coEvery { timeProvider.provide() } returns now
        coEvery {
            sessionsRepository.getSessionsCount(
                timeline = any(),
                includeSuccessful = false,
                includeFailed = true
            )
        } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.Failure>(result)
    }

    @Test
    fun `returns Failure when sessionsRepository issueSession throws`() = runTest {
        // GIVEN
        val error = RuntimeException("issue session error")
        coEvery { playerRepository.exists(tag = tag) } returns Result.success(true)
        coEvery { timeProvider.provide() } returns now
        coEvery {
            sessionsRepository.getSessionsCount(
                timeline = any(),
                includeSuccessful = false,
                includeFailed = true
            )
        } returns Result.success(0)
        coEvery { sessionTokenGenerator.generate() } returns token
        coEvery { ownershipTaskGenerator.generate() } returns ownershipTask
        coEvery { uuidProvider.provide() } returns Uuid.random()
        coEvery {
            sessionsRepository.issueSession(
                tag = tag,
                token = token,
                currentTime = now,
                duration = config.sessionDuration,
                challenge = any()
            )
        } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.Failure>(result)
    }

    @Test
    fun `returns Success with issued token and challenge`() = runTest {
        // GIVEN
        coEvery { playerRepository.exists(tag = tag) } returns Result.success(true)
        coEvery { timeProvider.provide() } returns now
        coEvery {
            sessionsRepository.getSessionsCount(
                timeline = any(),
                includeSuccessful = false,
                includeFailed = true
            )
        } returns Result.success(0)
        coEvery { sessionTokenGenerator.generate() } returns token
        coEvery { ownershipTaskGenerator.generate() } returns ownershipTask
        coEvery { uuidProvider.provide() } returns Uuid.random()
        coEvery {
            sessionsRepository.issueSession(
                tag = tag,
                token = token,
                currentTime = now,
                duration = config.sessionDuration,
                challenge = any()
            )
        } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(tag = tag, logger = logger)

        // THEN
        assertIs<AuthenticateUseCase.Result.Success>(result)
        assertEquals(expected = token, actual = result.token)
        assertEquals(expected = ownershipTask, actual = result.challenge.task)

        coVerify(exactly = 1) {
            sessionsRepository.issueSession(
                tag = tag,
                token = token,
                currentTime = now,
                duration = config.sessionDuration,
                challenge = any()
            )
        }
    }
}

package krawler.server.auth.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.server.auth.application.AccessToken
import krawler.server.auth.application.AuthenticationConfig
import krawler.server.auth.application.AuthenticationRepository
import krawler.server.auth.application.AuthenticationSessionToken
import krawler.server.auth.application.AuthenticationSessionsRepository
import krawler.server.auth.application.AuthenticationTokenGenerator
import krawler.server.auth.application.BattleRepository
import krawler.server.auth.application.CompleteAuthenticationUseCase
import krawler.server.auth.application.Hasher
import krawler.server.auth.application.RefreshToken
import krawler.server.auth.application.TimeProvider
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import krawler.server.auth.domain.ChallengedBrawlStarsPlayerTag
import krawler.server.auth.domain.OwnershipChallenge
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.AttemptsExceeded
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.AttemptFailed
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.Failure
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.SessionExpired
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.SessionNotFound
import krawler.server.auth.application.CompleteAuthenticationUseCase.Result.Success
import krawler.server.auth.domain.OwnershipChallengeResult
import krawler.server.auth.domain.OwnershipTaskBattle

class CompleteAuthenticationUseCaseTest {

    private val sessionsRepository = mockk<AuthenticationSessionsRepository>(relaxed = true)
    private val authenticationRepository = mockk<AuthenticationRepository>(relaxed = true)
    private val authTokenGenerator = mockk<AuthenticationTokenGenerator>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>()
    private val battleRepository = mockk<BattleRepository>(relaxed = true)
    private val hasher = mockk<Hasher>(relaxed = true)
    private val config = AuthenticationConfig(
        accessTokenDuration = 5.minutes,
        refreshTokenDuration = 10.minutes
    )

    private val useCase = CompleteAuthenticationUseCase(
        authenticationRepository = authenticationRepository,
        sessionsRepository = sessionsRepository,
        authTokenGenerator = authTokenGenerator,
        timeProvider = timeProvider,
        battleRepository = battleRepository,
        hasher = hasher,
        config = config
    )

    private val token = AuthenticationSessionToken(string = "token")
    private val tag = ChallengedBrawlStarsPlayerTag.createOrThrow("#ABC123")
    private val now = Instant.parse("2025-01-01T00:00:00Z")

    @Test
    fun `returns SessionNotFound when no challenge exists`() = runTest {
        // GIVEN
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<SessionNotFound>(result)
    }

    @Test
    fun `returns SessionNotFound when getChallenge throws`() = runTest {
        // GIVEN
        val ex = RuntimeException("repo failure")
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.failure(ex)

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<SessionNotFound>(result)
    }

    @Test
    fun `returns SessionExpired when challenge is expired`() = runTest {
        // GIVEN
        val challenge = mockChallenge(isExpired = true, isAttemptsExceeded = false)
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<SessionExpired>(result)
    }

    @Test
    fun `returns AttemptsExceeded when attempts exceeded`() = runTest {
        // GIVEN
        val challenge = mockChallenge(isExpired = false, isAttemptsExceeded = true)
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<AttemptsExceeded>(result)
    }

    @Test
    fun `returns AttemptFailed when challenge attempt fails`() = runTest {
        // GIVEN
        val challenge = mockChallenge(
            isExpired = false,
            isAttemptsExceeded = false,
            attemptResult = OwnershipChallengeResult.InvalidBrawler
        )
        val lastBattle = mockk<OwnershipTaskBattle>()
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now
        coEvery { battleRepository.getLastBattle(tag = tag) } returns Result.success(lastBattle)

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<AttemptFailed>(result)
        coVerify {
            @Suppress("RETURN_VALUE_NOT_USED")
            sessionsRepository.addAttempt(token = token)
        }
    }

    @Test
    fun `returns Success when challenge attempt succeeds`() = runTest {
        // GIVEN
        val challenge = mockChallenge(
            isExpired = false,
            isAttemptsExceeded = false,
            attemptResult = OwnershipChallengeResult.Success
        )
        val lastBattle = mockk<OwnershipTaskBattle>()
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now
        coEvery { battleRepository.getLastBattle(tag = tag) } returns Result.success(lastBattle)
        coEvery {
            authTokenGenerator.generateAccessToken(
                tag = tag,
                expiresAt = any()
            )
        } returns AccessToken(string = "access", expiresAt = now + 5.minutes)
        coEvery { authTokenGenerator.generateRefreshToken() } returns RefreshToken(string = "refresh")
        coEvery { authenticationRepository.issue(any()) } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<Success>(result)
    }

    @Test
    fun `returns Failure when lastBattle repository throws`() = runTest {
        // GIVEN
        val challenge = mockChallenge(
            isExpired = false,
            isAttemptsExceeded = false,
            attemptResult = OwnershipChallengeResult.InvalidBrawler
        )
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now
        val ex = RuntimeException("db failure")
        coEvery { battleRepository.getLastBattle(tag = tag) } returns Result.failure(ex)

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<Failure>(result)
    }

    @Test
    fun `returns Failure when authentication repository throws`() = runTest {
        // GIVEN
        val challenge = mockChallenge(
            isExpired = false,
            isAttemptsExceeded = false,
            attemptResult = OwnershipChallengeResult.Success
        )
        val lastBattle = mockk<OwnershipTaskBattle>()
        coEvery { sessionsRepository.getChallenge(token = token) } returns Result.success(challenge)
        coEvery { timeProvider.provide() } returns now
        coEvery { battleRepository.getLastBattle(tag = tag) } returns Result.success(lastBattle)
        coEvery {
            authTokenGenerator.generateAccessToken(
                tag = tag,
                expiresAt = any()
            )
        } returns AccessToken(string = "access", expiresAt = now + 5.minutes)
        coEvery { authTokenGenerator.generateRefreshToken() } returns RefreshToken(string = "refresh")
        coEvery { authenticationRepository.issue(any()) } returns Result.failure(RuntimeException("issue failed"))

        // WHEN
        val result = useCase.execute(token = token, logger = mockk(relaxed = true))

        // THEN
        assertIs<Failure>(result)
    }

    private fun mockChallenge(
        isExpired: Boolean,
        isAttemptsExceeded: Boolean,
        attemptResult: OwnershipChallengeResult = OwnershipChallengeResult.Success
    ): OwnershipChallenge = mockk {
        coEvery { isExpired(any()) } returns isExpired
        coEvery { isAttemptsExceeded() } returns isAttemptsExceeded
        coEvery { attempt(any(), any()) } returns attemptResult
        coEvery { playerTag } returns tag
    }
}

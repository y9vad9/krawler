package krawler.server.player.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.player.application.AuthenticatedPlayer
import krawler.server.player.application.GetPlayerProfileUseCase
import krawler.server.player.application.Player
import krawler.server.player.application.PlayerRepository
import krawler.server.player.application.PlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [GetPlayerProfileUseCase].
 */
class GetPlayerProfileUseCaseTest {

    private val repository: PlayerRepository = mockk()
    private val logger: Logger = mockk(relaxed = true)
    private val useCase = GetPlayerProfileUseCase(
        repository = repository,
        logger = logger,
    )

    /**
     * Ensures that when the requester is the same player and the repository
     * successfully returns player data, the use case emits [GetPlayerProfileUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester is the same player`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYER123")
        val principal = AuthenticatedPlayer(playerTag = tag)
        val player = mockk<Player>()
        coEvery {
            repository.getPlayer(tag)
        } returns Result.success(player)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.Success>(result)
        assertEquals(expected = player, actual = result.player)
        coVerify(exactly = 0) {
            val _ = repository.isWithinSameClubOrClubAlliance(any(), any())
        }
    }

    /**
     * Ensures that when the requester belongs to the same club or alliance,
     * and the repository returns player data, the use case emits [GetPlayerProfileUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester is in same alliance`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")
        val player = mockk<Player>()

        coEvery {
            repository.getPlayer(tag)
        } returns Result.success(player)
        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(true)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.Success>(result)
        assertEquals(expected = player, actual = result.player)
        coVerify(exactly = 1) {
            val _ = repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        }
    }

    /**
     * Ensures that when the repository throws while fetching player data,
     * the use case emits [GetPlayerProfileUseCase.Result.Failure] and logs the issue.
     */
    @Test
    fun `returns Failure when repository throws while fetching player`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYER")
        val principal = AuthenticatedPlayer(playerTag = tag)
        val throwable = IllegalStateException("Repository failure")

        coEvery {
            repository.getPlayer(tag)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to retrieve player from repository")
        }
    }

    /**
     * Ensures that when the repository returns null for the player,
     * the use case emits [GetPlayerProfileUseCase.Result.NotFound].
     */
    @Test
    fun `returns NotFound when player is missing`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYERX")
        val principal = AuthenticatedPlayer(playerTag = tag)

        coEvery {
            repository.getPlayer(tag)
        } returns Result.success(null)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.NotFound>(result)
        coVerify(exactly = 0) {
            val _ = repository.isWithinSameClubOrClubAlliance(any(), any())
        }
    }

    /**
     * Ensures that when the requester is not in the same club or alliance,
     * the use case emits [GetPlayerProfileUseCase.Result.PermissionDenied].
     */
    @Test
    fun `returns PermissionDenied when requester not permitted`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")
        val player = mockk<Player>()

        coEvery {
            repository.getPlayer(tag)
        } returns Result.success(player)
        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(false)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.PermissionDenied>(result)
    }

    /**
     * Ensures that when the repository throws during the alliance check,
     * the use case emits [GetPlayerProfileUseCase.Result.Failure] and logs the exception.
     */
    @Test
    fun `returns Failure when repository throws during alliance check`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")
        val player = mockk<Player>()
        val throwable = RuntimeException("Alliance check failed")

        coEvery {
            repository.getPlayer(tag)
        } returns Result.success(player)
        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerProfileUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to check whether two players are from the same club/alliance")
        }
    }
}

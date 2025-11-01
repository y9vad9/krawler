package krawler.server.player.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.player.application.AuthenticatedPlayer
import krawler.server.player.application.GetPlayerBrawlersUseCase
import krawler.server.player.application.PlayerBrawler
import krawler.server.player.application.PlayerRepository
import krawler.server.player.application.PlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [GetPlayerBrawlersUseCase].
 *
 * Covers success, permission denial, empty result, and failure cases.
 */
class GetPlayerBrawlersUseCaseTest {

    private val repository: PlayerRepository = mockk()
    private val logger: Logger = mockk(relaxed = true)
    private val useCase = GetPlayerBrawlersUseCase(
        repository = repository,
        logger = logger,
    )

    /**
     * Ensures that when the requester is the same player and
     * repository returns brawlers, the use case emits [GetPlayerBrawlersUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester is the same player and repository returns brawlers`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYER123")
        val principal = AuthenticatedPlayer(playerTag = tag)
        val brawlers = listOf(mockk<PlayerBrawler>(), mockk<PlayerBrawler>())

        coEvery {
            repository.getPlayerBrawlers(tag)
        } returns Result.success(brawlers)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.Success>(result)
        assertEquals(expected = brawlers, actual = result.brawlers)
        coVerify(exactly = 0) {
            val _ = repository.isWithinSameClubOrClubAlliance(any(), any())
        }
    }

    /**
     * Ensures that when the requester does not have permission
     * (not same player, not in same alliance), the use case returns [GetPlayerBrawlersUseCase.Result.PermissionDenied].
     */
    @Test
    fun `returns PermissionDenied when requester not permitted`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(false)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.PermissionDenied>(result)
        coVerify(exactly = 1) {
            val _ = repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        }
    }

    /**
     * Ensures that when the repository throws during the alliance check,
     * the use case emits [GetPlayerBrawlersUseCase.Result.Failure] and logs the issue.
     */
    @Test
    fun `returns Failure when repository throws on alliance check`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")
        val throwable = RuntimeException("DB failure")

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to check whether two players are from the same club/alliance")
        }
    }

    /**
     * Ensures that when the repository throws during brawler retrieval,
     * the use case emits [GetPlayerBrawlersUseCase.Result.Failure] and logs the exception.
     */
    @Test
    fun `returns Failure when repository throws while fetching brawlers`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYER")
        val principal = AuthenticatedPlayer(playerTag = tag)
        val throwable = IllegalStateException("Repository error")

        coEvery {
            repository.getPlayerBrawlers(tag)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to retrieve player brawlers from repository")
        }
    }

    /**
     * Ensures that when the requester belongs to the same club or alliance
     * and repository returns brawlers, the use case emits [GetPlayerBrawlersUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester in same alliance and brawlers found`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("AAA"))
        val tag = PlayerTag.createOrThrow("BBB")
        val brawlers = listOf(mockk<PlayerBrawler>())

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(true)

        coEvery {
            repository.getPlayerBrawlers(tag)
        } returns Result.success(brawlers)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.Success>(result)
        assertEquals(expected = brawlers, actual = result.brawlers)
        coVerify(exactly = 1) {
            val _ = repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        }
    }

    /**
     * Ensures that when the repository returns an empty brawler list,
     * the use case emits [GetPlayerBrawlersUseCase.Result.PlayerNotFound].
     */
    @Test
    fun `returns PlayerNotFound when repository returns empty brawler list`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYERX")
        val principal = AuthenticatedPlayer(playerTag = tag)

        coEvery {
            repository.getPlayerBrawlers(tag)
        } returns Result.success(emptyList())

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
        )

        // THEN
        assertIs<GetPlayerBrawlersUseCase.Result.PlayerNotFound>(result)
        coVerify(exactly = 0) {
            val _ = repository.isWithinSameClubOrClubAlliance(any(), any())
        }
    }
}

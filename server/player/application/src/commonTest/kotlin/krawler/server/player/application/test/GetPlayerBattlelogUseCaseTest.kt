package krawler.server.player.application.test

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import krawler.logger.Logger
import krawler.logger.withThrowable
import krawler.server.player.application.AuthenticatedPlayer
import krawler.server.player.application.GetPlayerBattlelogUseCase
import krawler.server.player.application.PaginationCursor
import krawler.server.player.application.PlayerRepository
import krawler.server.player.application.PlayerTag
import krawler.server.player.application.battle.Battle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [GetPlayerBattlelogUseCase].
 *
 * Covers success, failure, and permission-related scenarios.
 */
class GetPlayerBattlelogUseCaseTest {

    private val repository: PlayerRepository = mockk()
    private val logger: Logger = mockk(relaxed = true)
    private val useCase = GetPlayerBattlelogUseCase(
        repository = repository,
        logger = logger,
    )

    /**
     * Ensures that when the requester is the same player,
     * and the repository successfully returns battles,
     * the use case emits [GetPlayerBattlelogUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester is the same player and repository returns battles`() = runTest {
        // GIVEN
        val tag = PlayerTag.createOrThrow("PLAYERTAG")
        val principal = AuthenticatedPlayer(playerTag = tag)
        val cursor = PaginationCursor("cursor-123")
        val battles = listOf(mockk<Battle>(), mockk<Battle>())

        coEvery {
            repository.getPlayerBattleLog(tag = tag, cursor = cursor)
        } returns Result.success(
            PlayerRepository.BattlesWithCursor(
                previousCursor = null,
                nextCursor = PaginationCursor("next"),
                battles = battles,
            ),
        )

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
            cursor = cursor,
        )

        // THEN
        assertIs<GetPlayerBattlelogUseCase.Result.Success>(result)
        assertEquals(expected = battles, actual = result.battles)
        coVerify(exactly = 0) {
            val _ = repository.isWithinSameClubOrClubAlliance(any(), any())
        }
    }

    /**
     * Verifies that when the requester is not permitted
     * (not the same player and not in the same alliance),
     * the use case returns [GetPlayerBattlelogUseCase.Result.PermissionDenied].
     */
    @Test
    fun `returns PermissionDenied when requester not permitted`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("ABCD"))
        val tag = PlayerTag.createOrThrow("BCDEF")
        val cursor = PaginationCursor("c1")

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(false)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
            cursor = cursor,
        )

        // THEN
        assertIs<GetPlayerBattlelogUseCase.Result.PermissionDenied>(result)
        coVerify(exactly = 1) {
            val _ = repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        }
    }

    /**
     * Verifies that a failure in the alliance check
     * results in [GetPlayerBattlelogUseCase.Result.Failure]
     * and logs the error appropriately.
     */
    @Test
    fun `returns Failure when repository throws on club check`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("ABCD"))
        val tag = PlayerTag.createOrThrow("BCDEF")
        val cursor = PaginationCursor("c1")
        val throwable = RuntimeException("DB down")

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
            cursor = cursor,
        )

        // THEN
        assertIs<GetPlayerBattlelogUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to check whether two players are from the same club/alliance")
        }
    }

    /**
     * Verifies that a failure while fetching battles
     * results in [GetPlayerBattlelogUseCase.Result.Failure]
     * and logs the exception.
     */
    @Test
    fun `returns Failure when repository throws while fetching battles`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("ABCD"))
        val tag = PlayerTag.createOrThrow("ABCD")
        val cursor = PaginationCursor("c2")
        val throwable = IllegalStateException("Repository error")

        coEvery {
            repository.getPlayerBattleLog(tag = tag, cursor = cursor)
        } returns Result.failure(throwable)

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
            cursor = cursor,
        )

        // THEN
        assertIs<GetPlayerBattlelogUseCase.Result.Failure>(result)
        coVerify(exactly = 1) {
            logger.withField("player_tag", tag.stringWithTagPrefix)
                .withThrowable(throwable)
                .warning("Unable to retrieve player battles from repository")
        }
    }

    /**
     * Ensures that when the requester belongs to the same alliance
     * and the repository returns battles successfully,
     * the use case produces [GetPlayerBattlelogUseCase.Result.Success].
     */
    @Test
    fun `returns Success when requester in same alliance and battles found`() = runTest {
        // GIVEN
        val principal = AuthenticatedPlayer(playerTag = PlayerTag.createOrThrow("ABCD"))
        val tag = PlayerTag.createOrThrow("BCDEF")
        val cursor = PaginationCursor("c3")
        val battles = listOf(mockk<Battle>())

        coEvery {
            repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        } returns Result.success(true)
        coEvery {
            repository.getPlayerBattleLog(tag = tag, cursor = cursor)
        } returns Result.success(
            PlayerRepository.BattlesWithCursor(
                previousCursor = null,
                nextCursor = null,
                battles = battles,
            ),
        )

        // WHEN
        val result = useCase.execute(
            principal = principal,
            tag = tag,
            cursor = cursor,
        )

        // THEN
        assertIs<GetPlayerBattlelogUseCase.Result.Success>(result)
        assertEquals(expected = battles, actual = result.battles)
        coVerify(exactly = 1) {
            val _ = repository.isWithinSameClubOrClubAlliance(principal.playerTag, tag)
        }
    }
}

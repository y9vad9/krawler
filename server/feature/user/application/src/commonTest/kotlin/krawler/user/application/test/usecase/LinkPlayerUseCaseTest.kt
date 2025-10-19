package krawler.user.application.test.usecase

import krawler.user.application.PlayerRepository
import krawler.user.application.UserRepository
import krawler.user.application.LinkPlayerUseCase
import krawler.user.domain.User
import krawler.user.domain.BrawlStarsPlayer
import krawler.user.domain.UserUpdateEvent
import krawler.user.domain.BrawlStarsPlayerName
import krawler.user.domain.BrawlStarsPlayerTag
import krawler.user.domain.LinkedBrawlStarsPlayers
import krawler.user.domain.UserId
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.uuid.Uuid
import kotlinx.coroutines.test.runTest

class LinkPlayerUseCaseTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val playerRepository = mockk<PlayerRepository>(relaxed = true)
    private val useCase = LinkPlayerUseCase(userRepository, playerRepository)

    private val userId = UserId(Uuid.random())
    private val playerTag = BrawlStarsPlayerTag.createOrThrow("#ABCDEF")
    private val playerName = BrawlStarsPlayerName.createOrThrow("PlayerName")

    private val player = BrawlStarsPlayer(
        tag = playerTag,
        name = playerName,
    )

    private val linkedPlayers = mockk<LinkedBrawlStarsPlayers>(relaxed = true)

    private val userWithPlayerLinked = User(
        id = userId,
        linkedPlayers = linkedPlayers,
        linkedTelegram = mockk(relaxed = true),
    )

    private val userWithoutPlayerLinked = User(
        id = userId,
        linkedPlayers = linkedPlayers,
        linkedTelegram = mockk(relaxed = true),
    )

    @Test
    fun `execute returns UserNotFound when userRepository returns null user`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.UserNotFound>(result)
    }

    @Test
    fun `execute returns Failure when userRepository getUserBySystemId fails`() = runTest {
        // GIVEN
        val error = RuntimeException("DB error")
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.Failure>(result)
        assertSame(
            actual = result.throwable,
            expected = error,
        )
    }

    @Test
    fun `execute returns AlreadyLinked when player is already linked to user`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(userWithPlayerLinked)
        every { linkedPlayers.has(playerTag) } returns true

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.AlreadyLinked>(result)
    }

    @Test
    fun `execute returns PlayerNotFound when playerRepository returns null`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(userWithoutPlayerLinked)
        every { linkedPlayers.has(playerTag) } returns false
        coEvery { playerRepository.getPlayer(playerTag) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.PlayerNotFound>(result)
    }

    @Test
    fun `execute returns Failure when playerRepository getPlayer fails`() = runTest {
        // GIVEN
        val error = IllegalStateException("Player fetch failure")
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(userWithoutPlayerLinked)
        every { linkedPlayers.has(playerTag) } returns false
        coEvery { playerRepository.getPlayer(playerTag) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.Failure>(result)
        assertSame(
            actual = result.throwable,
            expected = error,
        )
    }

    @Test
    fun `execute returns Success when player is linked successfully`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(userWithoutPlayerLinked)
        every { linkedPlayers.has(playerTag) } returns false
        coEvery { playerRepository.getPlayer(playerTag) } returns Result.success(player)
        coEvery { userRepository.addLinkedPlayer(userId, player) } returns Result.success(Unit)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.Success>(result)
        assertEquals(
            expected = UserUpdateEvent.PlayerAdded(userId, player),
            actual = result.event,
        )
    }

    @Test
    fun `execute returns Failure when userRepository addLinkedPlayer fails`() = runTest {
        // GIVEN
        val error = IllegalArgumentException("Add player failed")
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(userWithoutPlayerLinked)
        every { linkedPlayers.has(playerTag) } returns false
        coEvery { playerRepository.getPlayer(playerTag) } returns Result.success(player)
        coEvery { userRepository.addLinkedPlayer(userId, player) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<LinkPlayerUseCase.Result.Failure>(result)
        assertSame(
            actual = result.throwable,
            expected = error,
        )
    }
}

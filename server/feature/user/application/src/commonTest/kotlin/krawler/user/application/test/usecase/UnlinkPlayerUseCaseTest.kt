package krawler.user.application.test.usecase

import krawler.user.application.UserRepository
import krawler.user.application.UnlinkPlayerUseCase
import krawler.user.domain.User
import krawler.user.domain.BrawlStarsPlayerTag
import krawler.user.domain.LinkedBrawlStarsPlayers
import krawler.user.domain.UserId
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.uuid.Uuid
import kotlinx.coroutines.test.runTest

class UnlinkPlayerUseCaseTest {
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val useCase = UnlinkPlayerUseCase(userRepository)

    private val userId = UserId(Uuid.random())
    private val playerTag = BrawlStarsPlayerTag.createOrThrow("PLAYER123")

    private val linkedPlayers = mockk<LinkedBrawlStarsPlayers>()

    private val user = User(
        id = userId,
        linkedTelegram = mockk(),
        linkedPlayers = linkedPlayers,
    )

    @Test
    fun `execute returns UserNotFound when user does not exist`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(null)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<UnlinkPlayerUseCase.Result.UserNotFound>(result)
    }

    @Test
    fun `execute returns Failure when userRepository throws`() = runTest {
        // GIVEN
        val error = IllegalStateException("oops")
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.failure(error)

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<UnlinkPlayerUseCase.Result.Failure>(result)
        assertSame(
            expected = error,
            actual = result.throwable,
        )
    }

    @Test
    fun `execute returns PlayerIsNotLinked when user does not have the player`() = runTest {
        // GIVEN
        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(user)
        every { linkedPlayers.has(playerTag) } returns false

        // WHEN
        val result = useCase.execute(userId, playerTag)

        // THEN
        assertIs<UnlinkPlayerUseCase.Result.PlayerIsNotLinked>(result)
    }

    // commented due to problems with mocking, see https://github.com/y9vad9/krawler/issues/17
//    @Test
//    fun `execute returns Failure when removing player fails`() = runTest {
//        // GIVEN
//        val updatedUser = mockk<User>()
//        val event = UserUpdateEvent.PlayerRemoved(userId, playerTag)
//        val error = RuntimeException("Removal failure")
//
//        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(user)
//        every { linkedPlayers.has(playerTag) } returns true
//        every { user.withoutLinkedPlayer(playerTag) } returns WithDomainUpdateEvent(updatedUser, event)
//        coEvery { userRepository.removeLinkedPlayer(userId, playerTag) } returns Result.failure(error)
//
//        // WHEN
//        val result = useCase.execute(userId, playerTag)
//
//        // THEN
//        assertIs<UnlinkPlayerUseCase.Result.Failure>(result)
//        assertSame(
//            expected = error,
//            actual = result.throwable,
//        )
//    }

//    @Test
//    fun `execute returns Success when player is removed successfully`() = runTest {
//        // GIVEN
//        val updatedUser = mockk<User>()
//        val event = UserUpdateEvent.PlayerRemoved(userId, playerTag)
//
//        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(user)
//        every { linkedPlayers.has(playerTag) } returns true
//        every { user.withoutLinkedPlayer(playerTag) } returns WithDomainUpdateEvent(updatedUser, event)
//        coEvery { userRepository.removeLinkedPlayer(userId, playerTag) } returns Result.success(Unit)
//
//        // WHEN
//        val result = useCase.execute(userId, playerTag)
//
//        // THEN
//        assertIs<UnlinkPlayerUseCase.Result.Success>(result)
//        assertSame(
//            expected = updatedUser,
//            actual = result.user,
//        )
//    }

//    @Test
//    fun `execute skips repository call when event is null`() = runTest {
//        // GIVEN
//        val updatedUser = mockk<User>()
//
//        coEvery { userRepository.getUserBySystemId(userId) } returns Result.success(user)
//        every { linkedPlayers.has(playerTag) } returns true
//        every { user.withoutLinkedPlayer(playerTag) } returns WithDomainUpdateEvent(updatedUser, null)
//
//        // WHEN
//        val result = useCase.execute(userId, playerTag)
//
//        // THEN
//        assertIs<UnlinkPlayerUseCase.Result.Success>(result)
//        assertSame(
//            expected = updatedUser,
//            actual = result.user,
//        )
//    }
}

package krawler.user.domain.test

import krawler.user.domain.User
import krawler.user.domain.BrawlStarsPlayer
import krawler.user.domain.LinkedTelegram
import krawler.user.domain.UserUpdateEvent
import krawler.user.domain.BrawlStarsPlayerName
import krawler.user.domain.BrawlStarsPlayerTag
import krawler.user.domain.LinkedBrawlStarsPlayers
import krawler.user.domain.LinkedTelegramChatId
import krawler.user.domain.LinkedTelegramUserName
import krawler.user.domain.UserId
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class UserTest {
    private val userId = UserId(uuid = Uuid.random())

    private val playerTag1 = BrawlStarsPlayerTag.createOrThrow("#AAA")
    private val playerTag2 = BrawlStarsPlayerTag.createOrThrow("#BBB")

    private val playerName1 = BrawlStarsPlayerName.createOrThrow("PlayerOne")
    private val playerName2 = BrawlStarsPlayerName.createOrThrow("PlayerTwo")

    private val player1 = BrawlStarsPlayer(tag = playerTag1, name = playerName1)
    private val player2 = BrawlStarsPlayer(tag = playerTag2, name = playerName2)

    private val initialTelegram = LinkedTelegram(
        id = LinkedTelegramChatId.createOrThrow(Random.nextLong(0, Long.MAX_VALUE)),
        name = LinkedTelegramUserName.createOrThrow("InitialTelegram"),
    )

    private val initialUser = User(
        id = userId,
        linkedPlayers = LinkedBrawlStarsPlayers(emptyList()),
        linkedTelegram = initialTelegram,
    )

    @Test
    fun `withNewLinkedTelegram updates telegram identity`() {
        // GIVEN
        val newTelegram = LinkedTelegram(
            id = LinkedTelegramChatId.createOrThrow(Random.nextLong(1, Long.MAX_VALUE)),
            name = LinkedTelegramUserName.createOrThrow("NewTG"),
        )

        // WHEN
        val (updatedUser, event) = initialUser.linkTelegram(newTelegram)

        // THEN
        assertEquals(
            expected = newTelegram,
            actual = updatedUser.linkedTelegram,
            message = "Telegram identity should be updated",
        )
        assertEquals(
            expected = userId,
            actual = updatedUser.id,
            message = "User ID should not change",
        )
        assertEquals(
            expected = initialUser.linkedPlayers,
            actual = updatedUser.linkedPlayers,
            message = "Linked players should remain unchanged",
        )
        assertEquals(
            expected = UserUpdateEvent.LinkedTelegramChanged(userId, newTelegram),
            actual = event,
            message = "Event should be LinkedTelegramChanged with correct values",
        )
    }

    @Test
    fun `withUpdatedLinkedTelegram updates telegram identity using transform`() {
        // GIVEN
        val updatedName = LinkedTelegramUserName.createOrThrow("UpdatedTG")

        // WHEN
        val (updatedUser, event) = initialUser.linkTelegram { it.withNewName(updatedName) }

        // THEN
        assertEquals(
            expected = updatedName,
            actual = updatedUser.linkedTelegram.name,
            message = "Telegram name should be updated",
        )
        assertEquals(
            expected = initialTelegram.id,
            actual = updatedUser.linkedTelegram.id,
            message = "Telegram ID should not change",
        )
        assertEquals(
            expected = userId,
            actual = updatedUser.id,
            message = "User ID should remain the same",
        )
        assertEquals(
            expected = UserUpdateEvent.LinkedTelegramChanged(userId, updatedUser.linkedTelegram),
            actual = event,
            message = "Event should reflect LinkedTelegramChanged",
        )
    }

    @Test
    fun `withAddedPlayer adds player when not linked`() {
        // WHEN
        val (updatedUser, event) = initialUser.assignPlayer(player1)

        // THEN
        assertTrue(
            actual = updatedUser.linkedPlayers.has(player1.tag),
            message = "Player should be linked",
        )
        assertEquals(
            expected = 1,
            actual = updatedUser.linkedPlayers.list.size,
            message = "There should be exactly one linked player",
        )
        assertEquals(
            expected = UserUpdateEvent.PlayerAdded(userId, player1),
            actual = event,
            message = "Event should be PlayerAdded with correct values",
        )
    }

    @Test
    fun `withAddedPlayer throws when player already linked`() {
        // GIVEN
        val (userWithPlayer, _) = initialUser.assignPlayer(player1)

        // WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            userWithPlayer.assignPlayer(player1)
        }

        // THEN
        assertContains(
            charSequence = error.message!!,
            other = "already linked",
        )
    }

    @Test
    fun `withRefreshedPlayer updates player when linked`() {
        // GIVEN
        val (userWithPlayer, _) = initialUser.assignPlayer(player1)
        val updatedPlayer = player1.name.let { originalName ->
            val newName = BrawlStarsPlayerName.createOrThrow("UpdatedName")
            BrawlStarsPlayer(tag = player1.tag, name = newName)
        }

        // WHEN
        val (refreshedUser, event) = userWithPlayer.updatePlayer(updatedPlayer)

        // THEN
        val refreshedPlayer = refreshedUser.linkedPlayers[player1.tag]
        assertNotNull(
            actual = refreshedPlayer,
            message = "Refreshed player must be present",
        )
        assertEquals(
            expected = updatedPlayer.name,
            actual = refreshedPlayer.name,
            message = "Player name should be updated",
        )
        assertEquals(
            expected = UserUpdateEvent.PlayerRefreshed(userId, updatedPlayer),
            actual = event,
            message = "Event should be PlayerRefreshed with correct values",
        )
    }

    @Test
    fun `withRefreshedPlayer throws when player not linked`() {
        // WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            initialUser.updatePlayer(player1)
        }

        // THEN
        assertContains(
            charSequence = error.message!!,
            other = "not linked",
        )
    }

    @Test
    fun `withoutLinkedPlayer removes linked player if present`() {
        // GIVEN
        val (userWithPlayer, _) = initialUser.assignPlayer(player1)

        // WHEN
        val (updatedUser, event) = userWithPlayer.unassignPlayer(player1.tag)

        // THEN
        assertFalse(
            actual = updatedUser.linkedPlayers.has(player1.tag),
            message = "Player should be removed",
        )
        assertEquals(
            expected = 0,
            actual = updatedUser.linkedPlayers.list.size,
            message = "Linked players list should be empty",
        )
        assertEquals(
            expected = UserUpdateEvent.PlayerRemoved(userId, player1.tag),
            actual = event,
            message = "Event should be PlayerRemoved with correct values",
        )
    }

    @Test
    fun `withoutLinkedPlayer returns same user if player not linked`() {
        // WHEN
        val (result, event) = initialUser.unassignPlayer(player1.tag)

        // THEN
        assertSame(
            expected = initialUser,
            actual = result,
            message = "User instance should be the same when no player is removed",
        )
        assertEquals(
            expected = null,
            actual = event,
            message = "Event should be NoOp when no player was removed",
        )
    }

    @Test
    fun `withoutAllLinkedPlayers clears all linked players`() {
        // GIVEN
        val (userWithPlayers, _) = initialUser.assignPlayer(player1).returning.assignPlayer(player2)

        // WHEN
        val (clearedUser, event) = userWithPlayers.unassignAllPlayers()

        // THEN
        assertTrue(
            actual = clearedUser.linkedPlayers.list.isEmpty(),
            message = "All linked players should be removed",
        )
        assertEquals(
            expected = UserUpdateEvent.AllPlayersRemoved(initialUser.id),
            actual = event,
            message = "Event should be AllPlayersRemoved",
        )
    }
}

package com.y9vad9.krawler.user.domain.test

import com.y9vad9.krawler.user.domain.User
import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.entity.LinkedTelegram
import com.y9vad9.krawler.user.domain.event.UserUpdateEvent
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerName
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.krawler.user.domain.value.LinkedBrawlStarsPlayers
import com.y9vad9.krawler.user.domain.value.LinkedTelegramChatId
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.krawler.user.domain.value.UserId
import com.y9vad9.valdi.createOrThrow
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

    private val playerTag1 = BrawlStarsPlayerTag.factory.createOrThrow("#AAA")
    private val playerTag2 = BrawlStarsPlayerTag.factory.createOrThrow("#BBB")

    private val playerName1 = BrawlStarsPlayerName.factory.createOrThrow("PlayerOne")
    private val playerName2 = BrawlStarsPlayerName.factory.createOrThrow("PlayerTwo")

    private val player1 = BrawlStarsPlayer(tag = playerTag1, name = playerName1)
    private val player2 = BrawlStarsPlayer(tag = playerTag2, name = playerName2)

    private val initialTelegram = LinkedTelegram(
        id = LinkedTelegramChatId.factory.createOrThrow(Random.nextLong(0, Long.MAX_VALUE)),
        name = LinkedTelegramUserName.factory.createOrThrow("InitialTelegram"),
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
            id = LinkedTelegramChatId.factory.createOrThrow(Random.nextLong(1, Long.MAX_VALUE)),
            name = LinkedTelegramUserName.factory.createOrThrow("NewTG"),
        )

        // WHEN
        val (updatedUser, event) = initialUser.withNewLinkedTelegram(newTelegram)

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
        val updatedName = LinkedTelegramUserName.factory.createOrThrow("UpdatedTG")

        // WHEN
        val (updatedUser, event) = initialUser.withUpdatedLinkedTelegram { it.withNewName(updatedName) }

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
        val (updatedUser, event) = initialUser.withAddedPlayer(player1)

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
        val (userWithPlayer, _) = initialUser.withAddedPlayer(player1)

        // WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            userWithPlayer.withAddedPlayer(player1)
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
        val (userWithPlayer, _) = initialUser.withAddedPlayer(player1)
        val updatedPlayer = player1.name.let { originalName ->
            val newName = BrawlStarsPlayerName.factory.createOrThrow("UpdatedName")
            BrawlStarsPlayer(tag = player1.tag, name = newName)
        }

        // WHEN
        val (refreshedUser, event) = userWithPlayer.withRefreshedPlayer(updatedPlayer)

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
            initialUser.withRefreshedPlayer(player1)
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
        val (userWithPlayer, _) = initialUser.withAddedPlayer(player1)

        // WHEN
        val (updatedUser, event) = userWithPlayer.withoutLinkedPlayer(player1.tag)

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
        val (result, event) = initialUser.withoutLinkedPlayer(player1.tag)

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
        val (userWithPlayers, _) = initialUser.withAddedPlayer(player1).returning.withAddedPlayer(player2)

        // WHEN
        val (clearedUser, event) = userWithPlayers.withoutAllLinkedPlayers()

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

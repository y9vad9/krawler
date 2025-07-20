package com.y9vad9.brawlex.user.domain.test

import com.y9vad9.brawlex.user.domain.LinkedPlayer
import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.brawlex.user.domain.value.LinkedPlayerName
import com.y9vad9.brawlex.user.domain.value.LinkedPlayerTag
import com.y9vad9.brawlex.user.domain.value.LinkedPlayers
import com.y9vad9.brawlex.user.domain.value.UserId
import com.y9vad9.brawlex.user.domain.value.UserName
import com.y9vad9.valdi.createOrThrow
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
    private val userName = UserName.factory.createOrThrow("InitialName")

    private val playerTag1 = LinkedPlayerTag.factory.createOrThrow("#AAA")
    private val playerTag2 = LinkedPlayerTag.factory.createOrThrow("#BBB")

    private val playerName1 = LinkedPlayerName.factory.createOrThrow("PlayerOne")
    private val playerName2 = LinkedPlayerName.factory.createOrThrow("PlayerTwo")

    private val player1 = LinkedPlayer(tag = playerTag1, name = playerName1)
    private val player2 = LinkedPlayer(tag = playerTag2, name = playerName2)

    private val initialUser = User(
        id = userId,
        name = userName,
        linkedPlayers = LinkedPlayers(emptyList()),
    )

    @Test
    fun `withNewName returns a new user with updated name`() {
        // GIVEN
        val newName = UserName.factory.createOrThrow("NewName")

        // WHEN
        val updatedUser = initialUser.withNewName(newName)

        // THEN
        assertEquals(
            expected = userId,
            actual = updatedUser.id,
            message = "User ID should not change",
        )
        assertEquals(
            expected = newName,
            actual = updatedUser.name,
            message = "User name should be updated",
        )
        assertEquals(
            expected = initialUser.linkedPlayers,
            actual = updatedUser.linkedPlayers,
            message = "Linked players should remain unchanged",
        )
    }

    @Test
    fun `withAddedPlayer adds player when not linked`() {
        // WHEN
        val updatedUser = initialUser.withAddedPlayer(player1)

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
    }

    @Test
    fun `withAddedPlayer throws when player already linked`() {
        // GIVEN
        val userWithPlayer = initialUser.withAddedPlayer(player1)

        // WHEN / THEN
        val error = assertFailsWith<IllegalArgumentException> {
            userWithPlayer.withAddedPlayer(player1)
        }
        assertContains(
            charSequence = error.message!!,
            other = "already linked",
        )
    }

    @Test
    fun `withRefreshedPlayer updates player when linked`() {
        // GIVEN
        val userWithPlayer = initialUser.withAddedPlayer(player1)
        val updatedPlayer = player1.copy(name = LinkedPlayerName.factory.createOrThrow("UpdatedName"))

        // WHEN
        val refreshedUser = userWithPlayer.withRefreshedPlayer(updatedPlayer)

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
    }

    @Test
    fun `withRefreshedPlayer throws when player not linked`() {
        // WHEN / THEN
        val error = assertFailsWith<IllegalArgumentException> {
            initialUser.withRefreshedPlayer(player1)
        }
        assertContains(
            charSequence = error.message!!,
            other = "not linked",
        )
    }

    @Test
    fun `withoutLinkedPlayer removes linked player if present`() {
        // GIVEN
        val userWithPlayer = initialUser.withAddedPlayer(player1)

        // WHEN
        val updatedUser = userWithPlayer.withoutLinkedPlayer(player1.tag)

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
    }

    @Test
    fun `withoutLinkedPlayer returns same user if player not linked`() {
        // WHEN
        val result = initialUser.withoutLinkedPlayer(player1.tag)

        // THEN
        assertSame(
            expected = initialUser,
            actual = result,
            message = "User instance should be the same when no player is removed",
        )
    }

    @Test
    fun `withoutAllLinkedPlayers clears all linked players`() {
        // GIVEN
        val userWithPlayers = initialUser.withAddedPlayer(player1).withAddedPlayer(player2)

        // WHEN
        val clearedUser = userWithPlayers.withoutAllLinkedPlayers()

        // THEN
        assertTrue(
            actual = clearedUser.linkedPlayers.list.isEmpty(),
            message = "All linked players should be removed",
        )
    }
}

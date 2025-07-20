package com.y9vad9.brawlex.user.domain.test.value

import com.y9vad9.brawlex.user.domain.LinkedPlayer
import com.y9vad9.brawlex.user.domain.value.LinkedPlayerName
import com.y9vad9.brawlex.user.domain.value.LinkedPlayerTag
import com.y9vad9.brawlex.user.domain.value.LinkedPlayers
import com.y9vad9.valdi.createOrThrow
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkedPlayersTest {
    private val player1 = LinkedPlayer(
        tag = LinkedPlayerTag.factory.createOrThrow("#AAA"),
        name = LinkedPlayerName.factory.createOrThrow("Player One"),
    )

    private val player2 = LinkedPlayer(
        tag = LinkedPlayerTag.factory.createOrThrow("#BBB"),
        name = LinkedPlayerName.factory.createOrThrow("Player Two"),
    )

    private val initial = LinkedPlayers(emptyList())

    @Test
    fun `adds player when not linked`() {
        val result = initial.with(player1)

        assertEquals(
            expected = 1,
            actual = result.list.size,
        )
        assertEquals(
            expected = player1,
            actual = result[player1.tag],
        )
    }

    @Test
    fun `throws when adding already linked player`() {
        val withPlayer = initial.with(player1)

        val error = assertFailsWith<IllegalArgumentException> {
            withPlayer.with(player1)
        }

        assertContains(
            charSequence = error.message!!,
            other = "already linked",
        )
    }

    @Test
    fun `refreshes linked player`() {
        val updated = player1.copy(
            name = LinkedPlayerName.factory.createOrThrow("Updated"),
        )

        val refreshed = initial.with(player1).withRefreshed(updated)

        assertEquals(
            expected = updated,
            actual = refreshed[player1.tag],
        )
    }

    @Test
    fun `throws when refreshing unlinked player`() {
        val error = assertFailsWith<IllegalArgumentException> {
            initial.withRefreshed(player1)
        }

        assertContains(
            charSequence = error.message!!,
            other = "not linked",
        )
    }

    @Test
    fun `removes linked player`() {
        val with = initial.with(player1)
        val result = with.without(player1.tag)

        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `remove does nothing if tag not linked`() {
        val result = initial.without(player1.tag)

        assertEquals(
            expected = initial,
            actual = result,
        )
    }

    @Test
    fun `replaces all linked players`() {
        val updated = initial.replacedWith(listOf(player1, player2))

        assertEquals(expected = 2, actual = updated.list.size)
        assertTrue(updated.has(player1.tag))
        assertTrue(updated.has(player2.tag))
    }

    @Test
    fun `withoutAll clears all players`() {
        val filled = initial.with(player1).with(player2)
        val result = filled.withoutAll()

        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `single and multiple detection works`() {
        val single = initial.with(player1)
        val multiple = single.with(player2)

        assertTrue(single.isLinkedToSingle)
        assertFalse(single.isLinkedToMultiple)
        assertFalse(single.isNotLinked)

        assertTrue(multiple.isLinkedToMultiple)
        assertFalse(multiple.isLinkedToSingle)
        assertFalse(multiple.isNotLinked)

        assertTrue(initial.isNotLinked)
        assertFalse(initial.isLinkedToSingle)
        assertFalse(initial.isLinkedToMultiple)
    }

    @Test
    fun `toString shows player name and tag`() {
        val list = initial.with(player1).with(player2)
        val result = list.toString()

        assertContains(
            charSequence = result,
            other = "Player One (#AAA)",
        )
        assertContains(
            charSequence = result,
            other = "Player Two (#BBB)",
        )
        assertContains(
            charSequence = result,
            other = "LinkedPlayers",
        )
        assertContains(
            charSequence = result,
            other = "count=2",
        )
    }
}

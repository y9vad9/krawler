package com.y9vad9.krawler.user.domain.test.value

import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerName
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.krawler.user.domain.value.LinkedBrawlStarsPlayers
import com.y9vad9.valdi.createOrThrow
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkedPlayersTest {
    private val player1 = BrawlStarsPlayer(
        tag = BrawlStarsPlayerTag.factory.createOrThrow("#AAA"),
        name = BrawlStarsPlayerName.factory.createOrThrow("Player One"),
    )

    private val player2 = BrawlStarsPlayer(
        tag = BrawlStarsPlayerTag.factory.createOrThrow("#BBB"),
        name = BrawlStarsPlayerName.factory.createOrThrow("Player Two"),
    )

    private val initial = LinkedBrawlStarsPlayers(emptyList())

    @Test
    fun `adds player when not linked`() {
        // GIVEN & WHEN
        val result = initial.withNew(player1)

        // THEN
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
        // GIVEN
        val withPlayer = initial.withNew(player1)

        // WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            withPlayer.withNew(player1)
        }

        // THEN
        assertContains(
            charSequence = error.message!!,
            other = "already linked",
        )
    }

    @Test
    fun `refreshes linked player`() {
        // GIVEN
        val updated = player1.withNewName(BrawlStarsPlayerName.factory.createOrThrow("Updated"))

        // WHEN
        val refreshed = initial.withNew(player1).withRefreshed(updated)

        // THEN
        assertEquals(
            expected = updated,
            actual = refreshed[player1.tag],
        )
    }

    @Test
    fun `throws when refreshing unlinked player`() {
        // GIVEN & WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            initial.withRefreshed(player1)
        }

        // THEN
        assertContains(
            charSequence = error.message!!,
            other = "not linked",
        )
    }

    @Test
    fun `removes linked player`() {
        // GIVEN
        val with = initial.withNew(player1)

        // WHEN
        val result = with.without(player1.tag)

        // THEN
        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `remove does nothing if tag not linked`() {
        // GIVEN & WHEN
        val result = initial.without(player1.tag)

        // THEN
        assertEquals(
            expected = initial,
            actual = result,
        )
    }

    @Test
    fun `replaces all linked players`() {
        // GIVEN & WHEN
        val updated = initial.replacedWith(listOf(player1, player2))

        // THEN
        assertEquals(expected = 2, actual = updated.list.size)
        assertTrue(updated.has(player1.tag))
        assertTrue(updated.has(player2.tag))
    }

    @Test
    fun `withoutAll clears all players`() {
        // GIVEN
        val filled = initial.withNew(player1).withNew(player2)

        // WHEN
        val result = filled.withoutAll()

        // THEN
        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `single and multiple detection works`() {
        // GIVEN
        val single = initial.withNew(player1)
        val multiple = single.withNew(player2)

        // THEN
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
        // GIVEN
        val list = initial.withNew(player1).withNew(player2)

        // WHEN
        val result = list.toString()

        // THEN
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

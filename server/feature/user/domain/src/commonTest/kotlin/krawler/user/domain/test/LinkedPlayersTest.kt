package krawler.user.domain.test

import krawler.user.domain.BrawlStarsPlayer
import krawler.user.domain.BrawlStarsPlayerName
import krawler.user.domain.BrawlStarsPlayerTag
import krawler.user.domain.LinkedBrawlStarsPlayers
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkedPlayersTest {
    private val player1 = BrawlStarsPlayer(
        tag = BrawlStarsPlayerTag.createOrThrow("#AAA"),
        name = BrawlStarsPlayerName.createOrThrow("Player One"),
    )

    private val player2 = BrawlStarsPlayer(
        tag = BrawlStarsPlayerTag.createOrThrow("#BBB"),
        name = BrawlStarsPlayerName.createOrThrow("Player Two"),
    )

    private val initial = LinkedBrawlStarsPlayers(emptyList())

    @Test
    fun `adds player when not linked`() {
        // GIVEN & WHEN
        val result = initial.assign(player1)

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
        val withPlayer = initial.assign(player1)

        // WHEN
        val error = assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            withPlayer.assign(player1)
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
        val updated = player1.rename(BrawlStarsPlayerName.createOrThrow("Updated"))

        // WHEN
        val refreshed = initial.assign(player1).update(updated)

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
            @Suppress("RETURN_VALUE_NOT_USED")
            initial.update(player1)
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
        val with = initial.assign(player1)

        // WHEN
        val result = with.unassign(player1.tag)

        // THEN
        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `remove does nothing if tag not linked`() {
        // GIVEN & WHEN
        val result = initial.unassign(player1.tag)

        // THEN
        assertEquals(
            expected = initial,
            actual = result,
        )
    }

    @Test
    fun `replaces all linked players`() {
        // GIVEN & WHEN
        val updated = initial.reassign(listOf(player1, player2))

        // THEN
        assertEquals(expected = 2, actual = updated.list.size)
        assertTrue(updated.has(player1.tag))
        assertTrue(updated.has(player2.tag))
    }

    @Test
    fun `withoutAll clears all players`() {
        // GIVEN
        val filled = initial.assign(player1).assign(player2)

        // WHEN
        val result = filled.unassignAll()

        // THEN
        assertTrue(result.list.isEmpty())
    }

    @Test
    fun `single and multiple detection works`() {
        // GIVEN
        val single = initial.assign(player1)
        val multiple = single.assign(player2)

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
        val list = initial.assign(player1).assign(player2)

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

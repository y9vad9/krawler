package krawler.user.domain.test

import krawler.user.domain.BrawlStarsPlayer
import krawler.user.domain.BrawlStarsPlayerName
import krawler.user.domain.BrawlStarsPlayerTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class LinkedPlayerTest {
    // GIVEN
    private val initialTag = BrawlStarsPlayerTag.createOrThrow("#ABC123")
    private val initialName = BrawlStarsPlayerName.createOrThrow("PlayerOne")
    private val linkedPlayer = BrawlStarsPlayer(tag = initialTag, name = initialName)

    @Test
    fun `withNewName returns a new instance with updated name`() {
        // GIVEN
        val newName = BrawlStarsPlayerName.createOrThrow("UpdatedName")

        // WHEN
        val updated = linkedPlayer.rename(newName)

        // THEN
        assertEquals(
            expected = newName,
            actual = updated.name,
            message = "Name should be updated",
        )
        assertEquals(
            expected = linkedPlayer.tag,
            actual = updated.tag,
            message = "Tag should remain the same",
        )
        assertNotSame(
            illegal = linkedPlayer,
            actual = updated,
            message = "Should return a new instance",
        )
    }

    @Test
    fun `withNewName returns same if name is unchanged`() {
        // WHEN
        val updated = linkedPlayer.rename(initialName)

        // THEN
        assertEquals(
            expected = linkedPlayer.name.string,
            actual = updated.name.string,
            message = "Name reference should be the same",
        )
        assertEquals(
            expected = linkedPlayer,
            actual = updated,
            message = "Instances should be structurally equal",
        )
    }
}

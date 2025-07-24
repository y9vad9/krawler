package com.y9vad9.brawlex.user.domain.test

import com.y9vad9.brawlex.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerName
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag
import com.y9vad9.valdi.createOrThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class LinkedPlayerTest {
    // GIVEN
    private val initialTag = BrawlStarsPlayerTag.factory.createOrThrow("#ABC123")
    private val initialName = BrawlStarsPlayerName.factory.createOrThrow("PlayerOne")
    private val linkedPlayer = BrawlStarsPlayer(tag = initialTag, name = initialName)

    @Test
    fun `withNewName returns a new instance with updated name`() {
        // GIVEN
        val newName = BrawlStarsPlayerName.factory.createOrThrow("UpdatedName")

        // WHEN
        val updated = linkedPlayer.withNewName(newName)

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
        val updated = linkedPlayer.withNewName(initialName)

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

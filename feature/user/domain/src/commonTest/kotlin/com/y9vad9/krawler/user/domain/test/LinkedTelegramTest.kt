package com.y9vad9.krawler.user.domain.test

import com.y9vad9.krawler.user.domain.entity.LinkedTelegram
import com.y9vad9.krawler.user.domain.value.LinkedTelegramChatId
import com.y9vad9.krawler.user.domain.value.LinkedTelegramUserName
import com.y9vad9.valdi.createOrThrow
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class LinkedTelegramTest {
    private val initialId = LinkedTelegramChatId.factory.createOrThrow(Random.nextLong(1, Long.MAX_VALUE))
    private val initialName = LinkedTelegramUserName.factory.createOrThrow("InitialName")
    private val linkedTelegram = LinkedTelegram(id = initialId, name = initialName)

    @Test
    fun `withNewId returns a new instance with updated id`() {
        // GIVEN
        val newId = LinkedTelegramChatId.factory.createOrThrow(Random.nextLong(1, Long.MAX_VALUE))

        // WHEN
        val updated = linkedTelegram.withNewId(newId)

        // THEN
        assertEquals(
            expected = newId,
            actual = updated.id,
            message = "ID should be updated",
        )
        assertEquals(
            expected = linkedTelegram.name,
            actual = updated.name,
            message = "Name should remain the same",
        )
        assertNotSame(
            illegal = linkedTelegram,
            actual = updated,
            message = "Should return a new instance",
        )
    }

    @Test
    fun `withNewName returns a new instance with updated name`() {
        // GIVEN
        val newName = LinkedTelegramUserName.factory.createOrThrow("UpdatedName")

        // WHEN
        val updated = linkedTelegram.withNewName(newName)

        // THEN
        assertEquals(
            expected = newName,
            actual = updated.name,
            message = "Name should be updated",
        )
        assertEquals(
            expected = linkedTelegram.id,
            actual = updated.id,
            message = "ID should remain the same",
        )
        assertNotSame(
            illegal = linkedTelegram,
            actual = updated,
            message = "Should return a new instance",
        )
    }

    @Test
    fun `withNewId returns same if id is the same`() {
        // WHEN
        val updated = linkedTelegram.withNewId(initialId)

        // THEN
        assertEquals(
            expected = linkedTelegram.id,
            actual = updated.id,
            message = "ID should be the same",
        )
        assertEquals(
            expected = linkedTelegram,
            actual = updated,
            message = "Should be structurally equal",
        )
    }

    @Test
    fun `withNewName returns same if name is the same`() {
        // WHEN
        val updated = linkedTelegram.withNewName(initialName)

        // THEN
        assertEquals(
            expected = linkedTelegram.name,
            actual = updated.name,
            message = "Name should be the same",
        )
        assertEquals(
            expected = linkedTelegram,
            actual = updated,
            message = "Should be structurally equal",
        )
    }
}

package krawler.server.player.application.test

import krawler.server.player.application.PlayerTrophies
import krawler.server.player.application.Trophies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for [PlayerTrophies].
 */
class PlayerTrophiesTest {

    @Test
    fun `create returns Success when current is less than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(100)
        val highest = Trophies.createOrThrow(200)

        // WHEN
        val result = PlayerTrophies.create(current, highest)

        // THEN
        assert(result.isSuccess)
        val trophies = result.getOrNull()
        assertNotNull(trophies)
        assertEquals(expected = current, actual = trophies.current)
        assertEquals(expected = highest, actual = trophies.highest)
    }

    @Test
    fun `create returns Success when current equals highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(150)
        val highest = Trophies.createOrThrow(150)

        // WHEN
        val result = PlayerTrophies.create(current, highest)

        // THEN
        assert(result.isSuccess)
        val trophies = result.getOrNull()
        assertNotNull(trophies)
        assertEquals(expected = current, actual = trophies.current)
        assertEquals(expected = highest, actual = trophies.highest)
    }

    @Test
    fun `create returns Failure when current is greater than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(300)
        val highest = Trophies.createOrThrow(200)

        // WHEN
        val result = PlayerTrophies.create(current, highest)

        // THEN
        assert(result.isFailure)
        val exception = result.exceptionOrNull()
        assertIs<IllegalArgumentException>(exception)
        assertEquals(expected = "Player highest can't be less than current.", actual = exception?.message)
    }

    @Test
    fun `createOrThrow succeeds when current is less than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(50)
        val highest = Trophies.createOrThrow(100)

        // WHEN
        val trophies = PlayerTrophies.createOrThrow(current, highest)

        // THEN
        assertEquals(expected = current, actual = trophies.current)
        assertEquals(expected = highest, actual = trophies.highest)
    }

    @Test
    fun `createOrThrow throws when current is greater than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(200)
        val highest = Trophies.createOrThrow(100)

        // THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerTrophies.createOrThrow(current, highest)
        }
    }

    @Test
    fun `createOrNull returns instance when current is less than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(10)
        val highest = Trophies.createOrThrow(20)

        // WHEN
        val trophies = PlayerTrophies.createOrNull(current, highest)

        // THEN
        assertNotNull(trophies)
        assertEquals(expected = current, actual = trophies.current)
        assertEquals(expected = highest, actual = trophies.highest)
    }

    @Test
    fun `createOrNull returns null when current is greater than highest`() {
        // GIVEN
        val current = Trophies.createOrThrow(50)
        val highest = Trophies.createOrThrow(25)

        // WHEN
        val trophies = PlayerTrophies.createOrNull(current, highest)

        // THEN
        assertNull(trophies)
    }
}

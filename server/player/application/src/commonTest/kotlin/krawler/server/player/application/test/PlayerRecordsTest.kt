package krawler.server.player.application.test

import krawler.server.player.application.PlayerRecords
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Unit tests for [PlayerRecords].
 */
class PlayerRecordsTest {

    @Test
    fun `isValid returns true for non-negative durations`() {
        // GIVEN
        val roboRumble = 30.seconds
        val bigBrawler = 45.seconds

        // THEN
        assertTrue(
            actual = PlayerRecords.isValid(
                bestRoboRumbleTime = roboRumble,
                bestTimeAsBigBrawler = bigBrawler
            )
        )
    }

    @Test
    fun `isValid returns false if any duration is negative`() {
        // GIVEN
        val roboRumbleNegative = (-5).seconds
        val bigBrawlerNegative = (-10).seconds
        val validDuration = 10.seconds

        // THEN
        assertFalse(
            actual = PlayerRecords.isValid(
                bestRoboRumbleTime = roboRumbleNegative,
                bestTimeAsBigBrawler = validDuration
            )
        )
        assertFalse(
            actual = PlayerRecords.isValid(
                bestRoboRumbleTime = validDuration,
                bestTimeAsBigBrawler = bigBrawlerNegative
            )
        )
        assertFalse(
            actual = PlayerRecords.isValid(
                bestRoboRumbleTime = roboRumbleNegative,
                bestTimeAsBigBrawler = bigBrawlerNegative
            )
        )
    }

    @Test
    fun `create returns success for valid durations`() {
        // GIVEN
        val roboRumble = 60.seconds
        val bigBrawler = 120.seconds

        // WHEN
        val result = PlayerRecords.create(
            roboRumble = roboRumble,
            bigBrawler = bigBrawler
        )

        // THEN
        assertTrue(result.isSuccess)
        val records = result.getOrNull()
        assertIs<PlayerRecords>(records)
        assertEquals(expected = roboRumble, actual = records.bestRoboRumbleTime)
        assertEquals(expected = bigBrawler, actual = records.bestTimeAsBigBrawler)
    }

    @Test
    fun `create returns failure for invalid durations`() {
        // GIVEN
        val roboRumble = (-5).seconds
        val bigBrawler = 30.seconds

        // WHEN
        val result = PlayerRecords.create(
            roboRumble = roboRumble,
            bigBrawler = bigBrawler
        )

        // THEN
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertIs<IllegalArgumentException>(exception)
        assertEquals("Durations must be positive.", exception.message)
    }

    @Test
    fun `createOrThrow returns PlayerRecords for valid durations`() {
        // GIVEN
        val roboRumble = 50.seconds
        val bigBrawler = 70.seconds

        // WHEN
        val records = PlayerRecords.createOrThrow(
            roboRumble = roboRumble,
            bigBrawler = bigBrawler
        )

        // THEN
        assertIs<PlayerRecords>(records)
        assertEquals(expected = roboRumble, actual = records.bestRoboRumbleTime)
        assertEquals(expected = bigBrawler, actual = records.bestTimeAsBigBrawler)
    }

    @Test
    fun `createOrThrow throws IllegalArgumentException for invalid durations`() {
        // GIVEN
        val roboRumble = (-1).seconds
        val bigBrawler = 10.seconds

        // THEN
        assertFailsWith<IllegalArgumentException> {
            val _ = PlayerRecords.createOrThrow(
                roboRumble = roboRumble,
                bigBrawler = bigBrawler
            )
        }
    }

    @Test
    fun `createOrNull returns PlayerRecords for valid durations`() {
        // GIVEN
        val roboRumble = 20.seconds
        val bigBrawler = 40.seconds

        // WHEN
        val records = PlayerRecords.createOrNull(
            roboRumble = roboRumble,
            bigBrawler = bigBrawler
        )

        // THEN
        assertNotNull(records)
        assertEquals(expected = roboRumble, actual = records.bestRoboRumbleTime)
        assertEquals(expected = bigBrawler, actual = records.bestTimeAsBigBrawler)
    }

    @Test
    fun `createOrNull returns null for invalid durations`() {
        // GIVEN
        val roboRumble = (-15).seconds
        val bigBrawler = (-10).seconds

        // WHEN
        val records = PlayerRecords.createOrNull(
            roboRumble = roboRumble,
            bigBrawler = bigBrawler
        )

        // THEN
        assertNull(records)
    }
}

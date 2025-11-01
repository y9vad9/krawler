package krawler.server.player.application.test

import krawler.server.player.application.PlayerRecords
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Unit tests for [PlayerRecords].
 */
class PlayerRecordsTest {

    @Test
    fun `given positive durations when PlayerRecords created then succeeds`() {
        // Given
        val roboRumble = 120.seconds
        val bigBrawler = 75.seconds

        // When
        val records = PlayerRecords(roboRumble, bigBrawler)

        // Then
        assertEquals(roboRumble, records.bestRoboRumbleTime)
        assertEquals(bigBrawler, records.bestTimeAsBigBrawler)
    }

    @Test
    fun `given zero roboRumble duration when PlayerRecords created then throws`() {
        // Given
        val roboRumble = Duration.ZERO
        val bigBrawler = 50.seconds

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerRecords(roboRumble, bigBrawler)
        }
        assertTrue(ex.message!!.contains("bestRoboRumbleTime should be positive"))
    }

    @Test
    fun `given zero bigBrawler duration when PlayerRecords created then throws`() {
        // Given
        val roboRumble = 50.seconds
        val bigBrawler = Duration.ZERO

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerRecords(roboRumble, bigBrawler)
        }
        assertTrue(ex.message!!.contains("bestTimeAsBigBrawler should be positive"))
    }

    @Test
    fun `given negative durations when PlayerRecords created then throws`() {
        // Given
        val roboRumble = (-10).seconds
        val bigBrawler = (-5).seconds

        // When & Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = PlayerRecords(roboRumble, bigBrawler)
        }
        assertTrue(ex.message!!.contains("bestRoboRumbleTime should be positive"))
    }
}

package krawler.server.player.application.test

import krawler.server.player.application.Trophies
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [Trophies].
 *
 * Ensures consistency across validation and factory methods,
 * as well as correct behavior of arithmetic and comparison operations.
 */
class TrophiesTest {

    @Test
    fun `creating Trophies with valid value succeeds`() {
        // Given a valid trophy count
        val raw = 500

        // When creating Trophies
        val trophies = Trophies(raw)

        // Then
        assertEquals(
            expected = raw,
            actual = trophies.rawInt,
            message = "Trophies should store the raw integer correctly"
        )
        assertEquals(
            expected = "500",
            actual = trophies.toString(),
            message = "toString() should return the string representation of rawInt"
        )
    }

    @Test
    fun `creating Trophies with negative value throws`() {
        // Given an invalid negative value
        val raw = -10

        // When / Then
        val ex = assertFailsWith<IllegalArgumentException> {
            val _ = Trophies(raw)
        }
        assertContains(
            charSequence = ex.message ?: "",
            other = "$raw",
            message = "Exception message should contain the invalid rawInt"
        )
    }

    @Test
    fun `plus operator sums two Trophies correctly`() {
        // Given two trophy counts
        val t1 = Trophies(100)
        val t2 = Trophies(200)

        // When adding them
        val sum = t1 + t2

        // Then
        assertEquals(
            expected = 300,
            actual = sum.rawInt,
            message = "Sum of Trophies should equal the sum of rawInt values"
        )
    }

    @Test
    fun `compareTo correctly compares Trophies`() {
        // Given two trophy counts
        val low = Trophies(50)
        val high = Trophies(150)

        // Then
        assertTrue(
            actual = low < high,
            message = "compareTo should indicate that 50 < 150"
        )
        assertTrue(
            actual = high > low,
            message = "compareTo should indicate that 150 > 50"
        )
        assertEquals(
            expected = 0,
            actual = Trophies(100).compareTo(Trophies(100)),
            message = "compareTo should return 0 for equal trophy counts"
        )
    }
}

package krawler.server.player.application.test

import krawler.server.player.application.VictoryAmount
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [VictoryAmount].
 *
 * Verifies that creation, validation, comparison, and arithmetic operations
 * behave correctly for both valid and invalid victory counts.
 */
class VictoryAmountTest {

    @Test
    fun `creating a VictoryAmount with non-negative value succeeds`() {
        // Given a valid number of victories
        val validValue = 42

        // When constructing a VictoryAmount
        val victoryAmount = VictoryAmount(int = validValue)

        // Then the raw value should match
        assertEquals(
            expected = validValue,
            actual = victoryAmount.int,
            message = "VictoryAmount.int should equal the input value"
        )
    }

    @Test
    fun `creating a VictoryAmount with negative value should throw exception`() {
        // Given a negative victory count
        val negativeValue = -1

        // When / Then
        val exception = assertFailsWith<IllegalArgumentException> {
            val _ = VictoryAmount(int = negativeValue)
        }

        assertContains(
            charSequence = exception.message ?: "",
            other = "zero or greater",
            message = "Exception message should indicate non-negative constraint"
        )
    }

    @Test
    fun `addition of VictoryAmounts returns correct sum`() {
        // Given two VictoryAmount instances
        val a = VictoryAmount(int = 10)
        val b = VictoryAmount(int = 15)

        // When adding them
        val sum = a + b

        // Then the result should be the sum of the two values
        assertEquals(
            expected = 25,
            actual = sum.int,
            message = "Sum of VictoryAmounts should equal the sum of raw values"
        )
    }

    @Test
    fun `subtraction of VictoryAmounts returns correct difference`() {
        // Given two VictoryAmount instances
        val a = VictoryAmount(int = 20)
        val b = VictoryAmount(int = 5)

        // When subtracting
        val diff = a - b

        // Then the result should be the difference of the two values
        assertEquals(
            expected = 15,
            actual = diff.int,
            message = "Difference of VictoryAmounts should equal the difference of raw values"
        )
    }

    @Test
    fun `compareTo returns correct ordering`() {
        // Given two VictoryAmount instances
        val lower = VictoryAmount(int = 3)
        val higher = VictoryAmount(int = 7)

        // Then comparison behaves correctly
        assertTrue(
            actual = lower < higher,
            message = "Lower VictoryAmount should be less than higher VictoryAmount"
        )

        assertTrue(
            actual = higher > lower,
            message = "Higher VictoryAmount should be greater than lower VictoryAmount"
        )

        val equal = VictoryAmount(int = 3)
        assertEquals(
            expected = 0,
            actual = lower.compareTo(equal),
            message = "Comparing equal VictoryAmounts should return 0"
        )
    }
}

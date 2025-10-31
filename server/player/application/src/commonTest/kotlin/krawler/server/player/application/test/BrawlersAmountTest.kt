package krawler.server.player.application.test

import krawler.server.player.application.BrawlersAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BrawlersAmountTest {

    @Test
    fun `isValid returns true for non-negative values`() {
        // GIVEN a list of valid victory counts
        val validCounts = listOf(0, 1, 1000, Int.MAX_VALUE)

        // WHEN we check validity
        validCounts.forEach { count ->
            // THEN it should return true
            assertTrue(actual = BrawlersAmount.isValid(input = count), message = "Expected $count to be valid")
        }
    }

    @Test
    fun `isValid returns false for negative values`() {
        // GIVEN a list of invalid victory counts
        val invalidCounts = listOf(-1, -100, Int.MIN_VALUE)

        // WHEN we check validity
        invalidCounts.forEach { count ->
            // THEN it should return false
            assertFalse(actual = BrawlersAmount.isValid(input = count), message = "Expected $count to be invalid")
        }
    }

    @Test
    fun `create returns success for valid input`() {
        // GIVEN a valid victory count
        val input = 42

        // WHEN we create a BrawlersAmount
        val result = BrawlersAmount.create(input = input)

        // THEN it should succeed and wrap the value
        assertTrue(actual = result.isSuccess)
        assertEquals(expected = 42, actual = result.getOrThrow().int)
    }

    @Test
    fun `create returns failure for negative input`() {
        // GIVEN a negative input
        val input = -5

        // WHEN we create a BrawlersAmount
        val result = BrawlersAmount.create(input = input)

        // THEN it should fail
        assertTrue(actual = result.isFailure)
        assertTrue(actual = result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `createOrThrow returns instance for valid input`() {
        // GIVEN a valid input
        val input = 10

        // WHEN we call createOrThrow
        val amount = BrawlersAmount.createOrThrow(input = input)

        // THEN it should return a valid BrawlersAmount
        assertEquals(expected = 10, actual = amount.int)
    }

    @Test
    fun `createOrThrow throws for invalid input`() {
        // GIVEN a negative input
        val input = -1

        // WHEN/THEN it should throw IllegalArgumentException
        assertFailsWith<IllegalArgumentException> {
            val _ = BrawlersAmount.createOrThrow(input = input)
        }
    }

    @Test
    fun `createOrNull returns instance for valid input`() {
        // GIVEN a valid input
        val input = 7

        // WHEN we call createOrNull
        val amount = BrawlersAmount.createOrNull(input = input)

        // THEN it should return a valid BrawlersAmount
        assertEquals(expected = 7, actual = amount?.int)
    }

    @Test
    fun `createOrNull returns null for invalid input`() {
        // GIVEN an invalid input
        val input = -3

        // WHEN we call createOrNull
        val amount = BrawlersAmount.createOrNull(input = input)

        // THEN it should return null
        assertNull(actual = amount)
    }

    @Test
    fun `compareTo compares BrawlersAmount numerically`() {
        // GIVEN two amounts
        val a = BrawlersAmount.createOrThrow(input = 5)
        val b = BrawlersAmount.createOrThrow(input = 10)

        // THEN compareTo should reflect numeric ordering
        assertTrue(actual = a < b)
        assertTrue(actual = b > a)
        assertEquals(expected = 0, actual = a.compareTo(a))
    }

    @Test
    fun `plus adds two BrawlersAmount correctly`() {
        // GIVEN two amounts
        val a = BrawlersAmount.createOrThrow(input = 3)
        val b = BrawlersAmount.createOrThrow(input = 7)

        // WHEN we add them
        val sum = a + b

        // THEN it should return correct sum
        assertEquals(expected = 10, actual = sum.int)
    }

    @Test
    fun `minus subtracts two BrawlersAmount correctly`() {
        // GIVEN two amounts
        val a = BrawlersAmount.createOrThrow(input = 10)
        val b = BrawlersAmount.createOrThrow(input = 4)

        // WHEN we subtract
        val diff = a - b

        // THEN it should return correct difference
        assertEquals(expected = 6, actual = diff.int)
    }

    @Test
    fun `minus throws when result would be negative`() {
        // GIVEN two amounts
        val a = BrawlersAmount.createOrThrow(input = 2)
        val b = BrawlersAmount.createOrThrow(input = 5)

        // WHEN/THEN subtracting should throw
        assertFailsWith<IllegalArgumentException> {
            val _ = a - b
        }
    }
}

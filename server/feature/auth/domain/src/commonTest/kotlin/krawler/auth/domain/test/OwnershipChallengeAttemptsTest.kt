package krawler.auth.domain.test

import krawler.auth.domain.OwnershipChallengeAttempts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class OwnershipChallengeAttemptsTest {

    @Test
    fun `should succeed for MIN_VALUE`() {
        // GIVEN
        val input = OwnershipChallengeAttempts.MIN_VALUE

        // WHEN
        val result = OwnershipChallengeAttempts.create(input)

        // THEN
        assertIs<OwnershipChallengeAttempts.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.int,
            message = "Expected value to match MIN_VALUE"
        )

        // Consistency checks
        assertEquals(
            expected = input,
            actual = OwnershipChallengeAttempts.createOrThrow(input).int
        )
        assertEquals(
            expected = input,
            actual = OwnershipChallengeAttempts.createOrNull(input)?.int
        )
    }

    @Test
    fun `should succeed for arbitrary value above minimum`() {
        // GIVEN
        val input = 5

        // WHEN
        val result = OwnershipChallengeAttempts.create(input)

        // THEN
        assertIs<OwnershipChallengeAttempts.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.int,
            message = "Expected value to match input"
        )

        // Consistency checks
        assertEquals(
            expected = input,
            actual = OwnershipChallengeAttempts.createOrThrow(input).int
        )
        assertEquals(
            expected = input,
            actual = OwnershipChallengeAttempts.createOrNull(input)?.int
        )
    }

    @Test
    fun `should fail for negative attempts`() {
        // GIVEN
        val input = -3

        // WHEN
        val result = OwnershipChallengeAttempts.create(input)

        // THEN
        assertIs<OwnershipChallengeAttempts.FactoryResult.InvalidMinValue>(result)

        // Consistency checks
        assertNull(
            actual = OwnershipChallengeAttempts.createOrNull(input),
            message = "Expected createOrNull to return null for negative input"
        )
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeAttempts.createOrThrow(input)
        }
    }

    @Test
    fun `should compare attempts correctly`() {
        // GIVEN
        val lower = OwnershipChallengeAttempts(int = 2)
        val higher = OwnershipChallengeAttempts(int = 4)
        val same = OwnershipChallengeAttempts(int = 2)

        // THEN
        assertEquals(
            expected = -1,
            actual = lower.compareTo(higher).coerceIn(-1, 1),
            message = "Lower should compare less than higher"
        )
        assertEquals(
            expected = 1,
            actual = higher.compareTo(lower).coerceIn(-1, 1),
            message = "Higher should compare greater than lower"
        )
        assertEquals(
            expected = 0,
            actual = lower.compareTo(same),
            message = "Equal values should compare as zero"
        )
    }

    @Test
    fun `should detect faulty validation logic that accepts below MIN_VALUE`() {
        // GIVEN
        val input = -1

        // WHEN
        val result = OwnershipChallengeAttempts.create(input)

        // THEN
        assertIs<OwnershipChallengeAttempts.FactoryResult.InvalidMinValue>(result)

        // Consistency checks
        assertNull(
            actual = OwnershipChallengeAttempts.createOrNull(input),
            message = "Expected createOrNull to return null for invalid input"
        )
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeAttempts.createOrThrow(input)
        }
    }
}

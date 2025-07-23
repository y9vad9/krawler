package com.y9vad9.brawlex.auth.domain.test.value

import com.y9vad9.brawlex.auth.domain.value.OwnershipChallengeAttempts
import com.y9vad9.brawlex.auth.domain.value.OwnershipChallengeAttempts.InvalidMinValue
import com.y9vad9.valdi.ValidationResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class OwnershipChallengeAttemptsTest {

    @Test
    fun `should succeed for MIN_VALUE`() {
        val result = OwnershipChallengeAttempts.factory.create(OwnershipChallengeAttempts.MIN_VALUE)

        assertIs<ValidationResult.Success<OwnershipChallengeAttempts>>(result)
        assertEquals(
            expected = OwnershipChallengeAttempts.MIN_VALUE,
            actual = result.value.int,
        )
    }

    @Test
    fun `should succeed for arbitrary value above minimum`() {
        val result = OwnershipChallengeAttempts.factory.create(5)

        assertIs<ValidationResult.Success<OwnershipChallengeAttempts>>(result)
        assertEquals(
            expected = 5,
            actual = result.value.int,
        )
    }

    @Test
    fun `should fail for negative attempts`() {
        val result = OwnershipChallengeAttempts.factory.create(-3)

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = InvalidMinValue,
            actual = result.error,
        )
    }

    @Test
    fun `should compare attempts correctly`() {
        val lower = OwnershipChallengeAttempts(int = 2)
        val higher = OwnershipChallengeAttempts(int = 4)

        assertEquals(
            expected = -1,
            actual = lower.compareTo(higher).coerceIn(-1, 1), // Normalizing result for stable comparison
        )
        assertEquals(
            expected = 1,
            actual = higher.compareTo(lower).coerceIn(-1, 1),
        )
        assertEquals(
            expected = 0,
            actual = lower.compareTo(OwnershipChallengeAttempts(int = 2)),
        )
    }

    @Test
    fun `should detect faulty validation logic that accepts MIN_VALUE and below`() {
        val result = OwnershipChallengeAttempts.factory.create(-1)

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = InvalidMinValue,
            actual = result.error,
        )
    }
}

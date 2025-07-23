package com.y9vad9.brawlex.auth.domain.test.value

import com.y9vad9.brawlex.auth.domain.value.OwnershipChallengeBotsAmount
import com.y9vad9.brawlex.auth.domain.value.OwnershipChallengeBotsAmount.ValueNotWithinRangeFailure
import com.y9vad9.valdi.ValidationResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class OwnershipChallengeBotsAmountTest {

    @Test
    fun `should succeed for MIN_VALUE`() {
        val result = OwnershipChallengeBotsAmount.factory.create(OwnershipChallengeBotsAmount.MIN_VALUE)

        assertIs<ValidationResult.Success<OwnershipChallengeBotsAmount>>(result)
        assertEquals(
            expected = OwnershipChallengeBotsAmount.MIN_VALUE,
            actual = result.value.int,
        )
    }

    @Test
    fun `should succeed for MAX_VALUE`() {
        val result = OwnershipChallengeBotsAmount.factory.create(OwnershipChallengeBotsAmount.MAX_VALUE)

        assertIs<ValidationResult.Success<OwnershipChallengeBotsAmount>>(result)
        assertEquals(
            expected = OwnershipChallengeBotsAmount.MAX_VALUE,
            actual = result.value.int,
        )
    }

    @Test
    fun `should succeed for arbitrary value in range`() {
        val result = OwnershipChallengeBotsAmount.factory.create(5)

        assertIs<ValidationResult.Success<OwnershipChallengeBotsAmount>>(result)
        assertEquals(
            expected = 5,
            actual = result.value.int,
        )
    }

    @Test
    fun `should fail for value below range`() {
        val result = OwnershipChallengeBotsAmount.factory.create(-1)

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = ValueNotWithinRangeFailure,
            actual = result.error,
        )
    }

    @Test
    fun `should fail for value above range`() {
        val result = OwnershipChallengeBotsAmount.factory.create(10)

        assertIs<ValidationResult.Failure<*>>(result)
        assertSame(
            expected = ValueNotWithinRangeFailure,
            actual = result.error,
        )
    }

    @Test
    fun `should define inclusive VALUE_RANGE`() {
        val range = OwnershipChallengeBotsAmount.VALUE_RANGE

        assertEquals(
            expected = OwnershipChallengeBotsAmount.MIN_VALUE,
            actual = range.first,
        )
        assertEquals(
            expected = OwnershipChallengeBotsAmount.MAX_VALUE,
            actual = range.last,
        )
    }
}

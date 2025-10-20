package krawler.server.auth.domain.test

import krawler.server.auth.domain.OwnershipChallengeBotsAmount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class OwnershipChallengeBotsAmountTest {

    @Test
    fun `should succeed for MIN_VALUE`() {
        // GIVEN
        val input = OwnershipChallengeBotsAmount.MIN_VALUE

        // WHEN
        val result = OwnershipChallengeBotsAmount.create(input)

        // THEN
        assertIs<OwnershipChallengeBotsAmount.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.int,
            message = "Expected value to match MIN_VALUE"
        )

        // Consistency checks
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrThrow(input).int
        )
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrNull(input)?.int
        )
    }

    @Test
    fun `should succeed for MAX_VALUE`() {
        // GIVEN
        val input = OwnershipChallengeBotsAmount.MAX_VALUE

        // WHEN
        val result = OwnershipChallengeBotsAmount.create(input)

        // THEN
        assertIs<OwnershipChallengeBotsAmount.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.int,
            message = "Expected value to match MAX_VALUE"
        )

        // Consistency checks
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrThrow(input).int
        )
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrNull(input)?.int
        )
    }

    @Test
    fun `should succeed for arbitrary value in range`() {
        // GIVEN
        val input = 5

        // WHEN
        val result = OwnershipChallengeBotsAmount.create(input)

        // THEN
        assertIs<OwnershipChallengeBotsAmount.FactoryResult.Success>(result)
        assertEquals(
            expected = input,
            actual = result.value.int,
            message = "Expected value to match input"
        )

        // Consistency checks
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrThrow(input).int
        )
        assertEquals(
            expected = input,
            actual = OwnershipChallengeBotsAmount.createOrNull(input)?.int
        )
    }

    @Test
    fun `should fail for value below range`() {
        // GIVEN
        val input = -1

        // WHEN
        val result = OwnershipChallengeBotsAmount.create(input)

        // THEN
        assertIs<OwnershipChallengeBotsAmount.FactoryResult.InvalidRange>(result)

        // Consistency checks
        assertNull(
            actual = OwnershipChallengeBotsAmount.createOrNull(input),
            message = "Expected createOrNull to return null for invalid input"
        )
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeBotsAmount.createOrThrow(input)
        }
    }

    @Test
    fun `should fail for value above range`() {
        // GIVEN
        val input = 10

        // WHEN
        val result = OwnershipChallengeBotsAmount.create(input)

        // THEN
        assertIs<OwnershipChallengeBotsAmount.FactoryResult.InvalidRange>(result)

        // Consistency checks
        assertNull(
            actual = OwnershipChallengeBotsAmount.createOrNull(input),
            message = "Expected createOrNull to return null for invalid input"
        )
        assertFailsWith<IllegalArgumentException> {
            @Suppress("RETURN_VALUE_NOT_USED")
            OwnershipChallengeBotsAmount.createOrThrow(input)
        }
    }

    @Test
    fun `should define inclusive VALUE_RANGE`() {
        // GIVEN
        val range = OwnershipChallengeBotsAmount.MIN_VALUE..OwnershipChallengeBotsAmount.MAX_VALUE

        // THEN
        assertEquals(
            expected = OwnershipChallengeBotsAmount.MIN_VALUE,
            actual = range.first,
            message = "Range start should match MIN_VALUE"
        )
        assertEquals(
            expected = OwnershipChallengeBotsAmount.MAX_VALUE,
            actual = range.last,
            message = "Range end should match MAX_VALUE"
        )
    }
}

package com.y9vad9.brawlex.user.domain.test.value

import com.y9vad9.brawlex.user.domain.value.UserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class UserIdTest {

    @Test
    fun `wraps UUID and delegates toString`() {
        // GIVEN
        val uuid = Uuid.random()

        // WHEN
        val userId = UserId(uuid)

        // THEN
        assertEquals(
            expected = uuid.toString(),
            actual = userId.toString(),
            message = "Expected UserId.toString() to delegate to UUID"
        )
        assertTrue(
            actual = userId.uuid == uuid,
            message = "Expected UserId to wrap provided UUID"
        )
    }
}

package com.y9vad9.krawler.auth.domain.test.value

import com.y9vad9.krawler.auth.domain.value.ChallengedUserId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ChallengedUserIdTest {

    @Test
    fun `wraps UUID and delegates toString`() {
        // GIVEN
        val uuid = Uuid.random()

        // WHEN
        val userId = ChallengedUserId(uuid)

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

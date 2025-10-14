package com.y9vad9.krawler.auth.domain.test.value

import com.y9vad9.krawler.auth.domain.value.ChallengeId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ChallengeIdTest {

    @Test
    fun `wraps UUID and delegates toString`() {
        // GIVEN
        val uuid = Uuid.random()

        // WHEN
        val userId = ChallengeId(uuid)

        // THEN
        assertEquals(
            expected = uuid.toString(),
            actual = userId.toString(),
            message = "Expected ChallengeId.toString() to delegate to UUID"
        )
        assertTrue(
            actual = userId.uuid == uuid,
            message = "Expected ChallengeId to wrap provided UUID"
        )
    }
}

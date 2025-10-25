package krawler.client.auth.domain.test

import krawler.client.auth.domain.ChallengeId
import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertEquals(
            expected = userId.uuid,
            actual = uuid,
            message = "Expected ChallengeId to wrap provided UUID",
        )
    }
}

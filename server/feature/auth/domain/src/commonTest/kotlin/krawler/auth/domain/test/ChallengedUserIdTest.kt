package krawler.auth.domain.test

import krawler.auth.domain.ChallengedUserId
import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertEquals(
            expected = userId.uuid,
            actual = uuid,
            message = "Expected UserId to wrap provided UUID",
        )
    }
}

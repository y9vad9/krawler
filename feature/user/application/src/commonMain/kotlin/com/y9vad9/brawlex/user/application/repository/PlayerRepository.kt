package com.y9vad9.brawlex.user.application.repository

import com.y9vad9.brawlex.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.brawlex.user.domain.value.BrawlStarsPlayerTag

interface PlayerRepository {
    /**
     * Retrieves a brawl stars player associated with the given [tag], if any.
     *
     * @return [Result] containing the [User] if found, or `null` if no such user exists.
     * A failure result indicates a retrieval error.
     */
    suspend fun getPlayer(tag: BrawlStarsPlayerTag): Result<BrawlStarsPlayer?>
}

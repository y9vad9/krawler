package com.y9vad9.krawler.user.application.repository

import com.y9vad9.krawler.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.krawler.user.domain.value.BrawlStarsPlayerTag

interface PlayerRepository {
    /**
     * Retrieves a brawl stars player associated with the given [tag], if any.
     *
     * @return [Result] containing the [BrawlStarsPlayer] if found, or `null` if no such user exists.
     * A failure result indicates a retrieval error.
     */
    suspend fun getPlayer(tag: BrawlStarsPlayerTag): Result<BrawlStarsPlayer?>
}

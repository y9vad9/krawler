package com.y9vad9.bcm.core.brawlstars.repository

import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag

interface BrawlStarsRepository {
    /**
     * Gets player from Brawl Stars API.
     *
     * @param tag tag in the Brawl Stars.
     * @param withInvalidate whether to ignore caching.
     *
     * @return [Result] of `null` if player with given tag does not exists or with [BrawlStarsPlayer] if does.
     * Otherwise returns Result.failure.
     */
    suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean = false): Result<Player?>

    /**
     * Gets club from Brawl Stars API.
     *
     * @param tag tag in the Brawl Stars.
     * @param withInvalidate whether to ignore caching.
     *
     * @return [Result] of `null` if player with given tag does not exists or with [BrawlStarsPlayer] if does.
     * Otherwise returns Result.failure.
     */
    suspend fun getClub(tag: ClubTag, withInvalidate: Boolean = false): Result<Club?>
}
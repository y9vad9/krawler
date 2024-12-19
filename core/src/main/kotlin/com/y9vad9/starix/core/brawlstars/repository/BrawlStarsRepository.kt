package com.y9vad9.starix.core.brawlstars.repository

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
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
    suspend fun getPlayer(tag: PlayerTag, withInvalidate: Boolean = false): Result<com.y9vad9.starix.core.brawlstars.entity.player.Player?>

    /**
     * Gets club from Brawl Stars API.
     *
     * @param tag tag in the Brawl Stars.
     * @param withInvalidate whether to ignore caching.
     *
     * @return [Result] of `null` if player with given tag does not exists or with [BrawlStarsPlayer] if does.
     * Otherwise, returns Result.failure.
     */
    suspend fun getClub(tag: ClubTag, withInvalidate: Boolean = false): Result<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club?>

    /**
     * Excuse player from requirements for a certain period of time.
     *
     * @param players Player's tags to be excused.
     * @param untilTime Until what time user should be excused.
     * @param currentTime Current unix time.
     */
    suspend fun excuse(
        players: List<PlayerTag>,
        clubTag: ClubTag,
        untilTime: UnixTime,
        currentTime: UnixTime,
    ): Result<Unit>

    /**
     * Gets the list of excused people.
     *
     * @param clubTag Club's tag from which to get list of excused people.
     * @param currentTime Current time to distinct actual excuses from unactual.
     */
    suspend fun getExcusedList(clubTag: ClubTag, currentTime: UnixTime): Result<Map<ClubMember, UnixTime>>

    /**
     * Unexcuses player from requirements.
     * @param playerTag Player's tag to be excused.
     */
    suspend fun unexcuse(playerTag: PlayerTag): Result<Unit>

    /**
     * Gets whether the specific player is excused.
     *
     * @param playerTag Player's tag which to should be checked
     * @param currentTime Current time to evict old excuses.
     */
    suspend fun isExcused(playerTag: PlayerTag, currentTime: UnixTime): Result<Boolean>
}
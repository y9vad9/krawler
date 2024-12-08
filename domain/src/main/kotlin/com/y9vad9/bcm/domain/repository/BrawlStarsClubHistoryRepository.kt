package com.y9vad9.bcm.domain.repository

import com.timemates.backend.time.UnixTime
import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag

interface BrawlStarsClubHistoryRepository {
    /**
     * Gets the list of allowed clubs within the system.
     */
    suspend fun saveToHistory(time: UnixTime, clubs: List<BrawlStarsClub>)

    suspend fun getFirstSavedAllowedClubsAfter(tags: List<ClubTag>, after: UnixTime): List<BrawlStarsClub>
    suspend fun getLastSavedAllowedClubsBefore(tags: List<ClubTag>, before: UnixTime): List<BrawlStarsClub>
}

suspend fun BrawlStarsClubHistoryRepository.getLatest(
    tags: List<ClubTag>,
): List<BrawlStarsClub> {
    return getLastSavedAllowedClubsBefore(tags, UnixTime.INFINITE)
}
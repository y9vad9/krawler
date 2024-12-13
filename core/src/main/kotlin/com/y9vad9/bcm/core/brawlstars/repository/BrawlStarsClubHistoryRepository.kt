package com.y9vad9.bcm.core.brawlstars.repository

import com.timemates.backend.time.UnixTime
import com.y9vad9.bcm.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag

interface BrawlStarsClubHistoryRepository {
    /**
     * Gets the list of allowed clubs within the system.
     */
    suspend fun saveToHistory(time: UnixTime, clubs: List<Club>)

    suspend fun getFirstSavedAllowedClubsAfter(tags: List<ClubTag>, after: UnixTime): List<Club>
    suspend fun getLastSavedAllowedClubsBefore(tags: List<ClubTag>, before: UnixTime): List<Club>
}

suspend fun BrawlStarsClubHistoryRepository.getLatest(
    tags: List<ClubTag>,
): List<Club> {
    return getLastSavedAllowedClubsBefore(tags, UnixTime.INFINITE)
}
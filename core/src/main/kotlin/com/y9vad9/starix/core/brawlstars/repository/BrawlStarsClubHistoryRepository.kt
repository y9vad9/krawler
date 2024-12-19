package com.y9vad9.starix.core.brawlstars.repository

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag

interface BrawlStarsClubHistoryRepository {
    /**
     * Gets the list of allowed clubs within the system.
     */
    suspend fun saveToHistory(time: UnixTime, clubs: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>)

    suspend fun getFirstSavedAllowedClubsAfter(tags: List<ClubTag>, after: UnixTime): List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>
    suspend fun getLastSavedAllowedClubsBefore(tags: List<ClubTag>, before: UnixTime): List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>
}

suspend fun BrawlStarsClubHistoryRepository.getLatest(
    tags: List<ClubTag>,
): List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club> {
    return getLastSavedAllowedClubsBefore(tags, UnixTime.INFINITE)
}
package com.y9vad9.starix.data

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.repository.BrawlStarsClubHistoryRepository
import com.y9vad9.bcm.data.database.BSClubHistoryTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ClubHistoryRepositoryImpl(
    private val historyTable: BSClubHistoryTable,
    private val json: Json,
) : BrawlStarsClubHistoryRepository {
    override suspend fun saveToHistory(time: UnixTime, clubs: List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>) {
        clubs.forEach { club ->
            historyTable.create(
                clubTag = club.tag.toString(),
                json = json.encodeToString<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club>(club),
                unixTime = time.inMilliseconds,
            )
        }
    }

    override suspend fun getFirstSavedAllowedClubsAfter(tags: List<ClubTag>, after: UnixTime): List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club> {
        return tags.mapNotNull { tag ->
            historyTable.getJsonOfFirstAfter(tag.toString(), after.inMilliseconds)?.let {
                json.decodeFromString(it)
            }
        }
    }

    override suspend fun getLastSavedAllowedClubsBefore(tags: List<ClubTag>, before: UnixTime): List<_root_ide_package_.com.y9vad9.starix.core.brawlstars.entity.club.Club> {
        return tags.mapNotNull { tag ->
            historyTable.getJsonOfLastBefore(tag.toString(), before.inMilliseconds)?.let {
                json.decodeFromString(it)
            }
        }
    }

}
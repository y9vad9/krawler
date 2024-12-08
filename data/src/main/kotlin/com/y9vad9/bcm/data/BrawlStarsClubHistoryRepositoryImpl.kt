package com.y9vad9.bcm.data

import com.timemates.backend.time.UnixTime
import com.y9vad9.bcm.data.brawlstars.entity.BsClub
import com.y9vad9.bcm.data.brawlstars.entity.serializable
import com.y9vad9.bcm.data.database.BSClubHistoryTable
import com.y9vad9.bcm.domain.entity.Club
import com.y9vad9.bcm.domain.entity.brawlstars.BrawlStarsClub
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.repository.BrawlStarsClubHistoryRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BrawlStarsClubHistoryRepositoryImpl(
    private val historyTable: BSClubHistoryTable,
    private val json: Json,
) : BrawlStarsClubHistoryRepository {
    override suspend fun saveToHistory(time: UnixTime, clubs: List<BrawlStarsClub>) {
        clubs.forEach { club ->
            historyTable.create(
                clubTag = club.tag.toString(),
                json = json.encodeToString<BsClub>(club.serializable()),
                unixTime = time.inMilliseconds,
            )
        }
    }

    override suspend fun getFirstSavedAllowedClubsAfter(tags: List<ClubTag>, after: UnixTime): List<BrawlStarsClub> {
        return tags.mapNotNull { tag ->
            historyTable.getJsonOfFirstAfter(tag.toString(), after.inMilliseconds)?.let {
                json.decodeFromString(it)
            }
        }
    }

    override suspend fun getLastSavedAllowedClubsBefore(tags: List<ClubTag>, before: UnixTime): List<BrawlStarsClub> {
        return tags.mapNotNull { tag ->
            historyTable.getJsonOfLastBefore(tag.toString(), before.inMilliseconds)?.let {
                json.decodeFromString(it)
            }
        }
    }

}
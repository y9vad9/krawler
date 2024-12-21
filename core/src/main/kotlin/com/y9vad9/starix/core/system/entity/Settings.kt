package com.y9vad9.starix.core.system.entity

import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.common.entity.value.CustomMessage
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val admins: List<TelegramUserId> = emptyList(),
    val allowedClubs: Map<ClubTag, ClubSettings> = emptyMap(),
    val contactsInfo: LocalizableEntity<CustomMessage>? = null
)

fun Settings.isAdminIn(clubTag: ClubTag, id: TelegramUserId): Boolean {
    return id in admins && id in allowedClubs[clubTag]?.admins.orEmpty()
}

fun Settings.isClubAllowed(clubTag: ClubTag): Boolean {
    return allowedClubs.containsKey(clubTag)
}
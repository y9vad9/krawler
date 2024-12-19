package com.y9vad9.starix.core.brawlstars.entity.club.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClubType {
    @SerialName("open")
    OPEN,
    @SerialName("invite_only")
    INVITE_ONLY,
    @SerialName("closed")
    CLOSED,
    @SerialName("unknown")
    UNKNOWN,
}
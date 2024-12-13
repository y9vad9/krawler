package com.y9vad9.bcm.core.brawlstars.entity.club.value

import kotlinx.serialization.Serializable

@Serializable
enum class ClubType {
    OPEN, INVITE_ONLY, CLOSED, UNKNOWN,
}
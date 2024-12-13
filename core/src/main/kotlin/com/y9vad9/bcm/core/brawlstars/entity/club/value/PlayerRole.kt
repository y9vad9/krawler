package com.y9vad9.bcm.core.brawlstars.entity.club.value

import kotlinx.serialization.Serializable

@Serializable
enum class PlayerRole {
    PRESIDENT, NOT_MEMBER, MEMBER, VICE_PRESIDENT, SENIOR, UNKNOWN,
}
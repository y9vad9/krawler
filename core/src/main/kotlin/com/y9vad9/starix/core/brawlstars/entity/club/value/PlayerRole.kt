package com.y9vad9.starix.core.brawlstars.entity.club.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PlayerRole {
    @SerialName("president")
    PRESIDENT,
    @SerialName("not_member")
    NOT_MEMBER,
    @SerialName("member")
    MEMBER,
    @SerialName("vice_president")
    VICE_PRESIDENT,
    @SerialName("senior")
    SENIOR,
    @SerialName("unknown")
    UNKNOWN,
}
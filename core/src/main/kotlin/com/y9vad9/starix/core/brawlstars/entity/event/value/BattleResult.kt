package com.y9vad9.starix.core.brawlstars.entity.event.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BattleResult {
    @SerialName("victory")
    VICTORY,

    @SerialName("defeat")
    DEFEAT
}
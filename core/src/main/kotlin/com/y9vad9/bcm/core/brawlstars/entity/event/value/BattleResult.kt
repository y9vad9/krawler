package com.y9vad9.bcm.core.brawlstars.entity.event.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BattleResult {
    @SerialName("victory")
    VICTORY,

    @SerialName("defeat")
    DEFEAT
}
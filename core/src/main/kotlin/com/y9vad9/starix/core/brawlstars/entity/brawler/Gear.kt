package com.y9vad9.starix.core.brawlstars.entity.brawler

import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.GearName
import com.y9vad9.bcm.core.brawlstars.entity.event.value.EventSlotId
import kotlinx.serialization.Serializable

@Serializable
data class Gear(
    val id: EventSlotId,
    val name: GearName,
)
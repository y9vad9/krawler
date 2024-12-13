package com.y9vad9.bcm.core.brawlstars.entity.brawler

import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.GadgetId
import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.GadgetName
import kotlinx.serialization.Serializable

@Serializable
data class Gadget(
    val id: GadgetId,
    val name: GadgetName,
)
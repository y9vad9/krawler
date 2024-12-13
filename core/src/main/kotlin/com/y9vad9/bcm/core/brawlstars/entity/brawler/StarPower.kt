package com.y9vad9.bcm.core.brawlstars.entity.brawler

import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.StarPowerId
import com.y9vad9.bcm.core.brawlstars.entity.brawler.value.StarPowerName
import kotlinx.serialization.Serializable

@Serializable
data class StarPower(
    val id: StarPowerId,
    val name: StarPowerName,
)
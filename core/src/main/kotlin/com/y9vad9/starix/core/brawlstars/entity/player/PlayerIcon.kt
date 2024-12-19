package com.y9vad9.starix.core.brawlstars.entity.player

import com.y9vad9.bcm.core.brawlstars.entity.player.value.IconId
import kotlinx.serialization.Serializable

@Serializable
data class PlayerIcon(
    val id: IconId,
)
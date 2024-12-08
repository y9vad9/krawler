package com.y9vad9.bcm.data.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class BsBattle(
    val battleTime: String,
    val event: BsEvent
)
package com.y9vad9.bcm.data.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class BsEvent(
    val mode: String,
    val id: Int,
    val map: Map<String, String>
)
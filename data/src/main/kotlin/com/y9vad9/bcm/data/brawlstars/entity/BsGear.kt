package com.y9vad9.bcm.data.brawlstars.entity

import kotlinx.serialization.Serializable

@Serializable
data class BsGear(
    val id: Int,
    val name: String,
    val level: Int? = null
)
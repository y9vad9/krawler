package com.y9vad9.bcm.core.brawlstars.exception

import kotlinx.serialization.Serializable

@Serializable
public data class ClientError(
    val reason: String,
    override val message: String? = null,
    val type: String? = null,
) : Exception()
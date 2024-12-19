package com.y9vad9.starix.core.brawlstars.exception

import kotlinx.serialization.Serializable

@Serializable
data class ClientError(
    val reason: String,
    override val message: String? = null,
    val type: String? = null,
) : Exception()
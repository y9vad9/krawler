package com.y9vad9.bcm.data.brawlstars.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val items: List<T>? = null,
    val paging: Cursors? = null,
)
package com.y9vad9.starix.data.brawlstars.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val items: List<T>? = null,
    val paging: Cursors? = null,
)
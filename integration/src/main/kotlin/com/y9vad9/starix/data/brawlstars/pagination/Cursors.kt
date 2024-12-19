package com.y9vad9.starix.data.brawlstars.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Cursors(
    val before: Cursor?,
    val after: Cursor?,
)
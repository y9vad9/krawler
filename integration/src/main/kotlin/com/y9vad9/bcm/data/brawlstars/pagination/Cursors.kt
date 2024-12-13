package com.y9vad9.bcm.data.brawlstars.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Cursors(
    val before: Cursor?,
    val after: Cursor?,
)
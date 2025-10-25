package krawler.server.brawlstars.client

import kotlinx.serialization.Serializable

@Serializable
internal data class RawItemsResponse<T>(
    val items: List<T>,
)

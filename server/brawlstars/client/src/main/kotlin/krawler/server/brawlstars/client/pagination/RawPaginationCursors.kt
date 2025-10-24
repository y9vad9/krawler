package krawler.server.brawlstars.client.pagination

import kotlinx.serialization.Serializable

/**
 * Represents pagination cursors used for navigating through paginated API results.
 *
 * @property before Optional cursor pointing to the item **before** the current page.
 * @property after Optional cursor pointing to the item **after** the current page.
 */
@Serializable
data class RawPaginationCursors(
    val before: String? = null,
    val after: String? = null,
)

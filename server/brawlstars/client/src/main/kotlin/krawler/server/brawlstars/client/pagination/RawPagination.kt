package krawler.server.brawlstars.client.pagination

import kotlinx.serialization.Serializable

/**
 * Represents pagination information for paginated API responses.
 *
 * @property cursors The [RawPaginationCursors] used to navigate through pages of results.
 */
@Serializable
data class RawPagination(
    val cursors: RawPaginationCursors = RawPaginationCursors(),
)

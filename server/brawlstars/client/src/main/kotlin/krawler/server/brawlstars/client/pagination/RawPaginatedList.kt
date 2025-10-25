package krawler.server.brawlstars.client.pagination

import kotlinx.serialization.Serializable

/**
 * Represents a paginated list of items returned by the API.
 *
 * Provides the items as a read-only [List] and includes pagination information
 * through the [paging] property.
 *
 * @param T The type of items contained in the list.
 * @param items The list of items for the current page.
 * @property paging The [RawPagination] information containing cursors for navigating pages.
 */
@Serializable
data class RawPaginatedList<T>(
    private val items: List<T>,
    val paging: RawPagination = RawPagination(),
) : List<T> by items

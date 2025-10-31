package krawler.server.player.application

/**
 * Represents an opaque pagination cursor used to retrieve the next set of results.
 *
 * @property rawString The underlying string value of the cursor.
 */
@JvmInline
value class PaginationCursor(val rawString: String)

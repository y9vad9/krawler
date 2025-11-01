package krawler.server.player.application.brawler

/**
 * Represents a Brawler's name.
 *
 * This inline value class wraps a [String] and allows for lexicographic comparison
 * between brawler names. Useful for sorting or ensuring type safety across the domain.
 *
 * Instances are compared based on the wrapped string value.
 */
@JvmInline
value class BrawlerName(
    /** The raw string value of the brawler's name. */
    val rawString: String
) : Comparable<BrawlerName> {

    /**
     * Compares this [BrawlerName] with [other] based on lexicographic order of the underlying [rawString].
     */
    override fun compareTo(other: BrawlerName): Int = rawString.compareTo(other.rawString)

    /**
     * Returns the string representation of the brawler name.
     */
    override fun toString(): String = rawString
}

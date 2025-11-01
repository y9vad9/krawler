package krawler.server.player.application.brawler

/**
 * Represents the name of a Star Power in Brawl Stars.
 *
 * This inline value class wraps a [String] value to provide type safety and
 * lexicographic comparability between star power names.
 *
 * Star Powers are unique passive abilities unlocked for brawlers, and each has a
 * distinctive name which is represented by this class.
 *
 * @property rawString The raw string value of the star power's name. Used for display and comparison.
 */
@JvmInline
value class BrawlerStarPowerName(
    val rawString: String
) : Comparable<BrawlerStarPowerName> {

    /**
     * Compares this [BrawlerStarPowerName] with [other] based on the lexicographic order of the underlying [rawString].
     *
     * @param other The other star power name to compare against.
     * @return A negative integer, zero, or a positive integer as this name is less than, equal to,
     * or greater than [other].
     */
    override fun compareTo(other: BrawlerStarPowerName): Int = rawString.compareTo(other.rawString)

    /**
     * Returns the raw string representation of the star power name.
     */
    override fun toString(): String = rawString
}

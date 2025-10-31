package krawler.server.player.application.brawler

/**
 * Represents a Brawler Gadget's name.
 *
 * This inline value class wraps a [String] and allows for lexicographic comparison
 * between brawler names. Useful for sorting or ensuring type safety across the domain.
 *
 * Instances are compared based on the wrapped string value.
 */
@JvmInline
value class BrawlerGadgetName(
    /** The raw string value of the gadget's name. */
    val rawString: String,
) : Comparable<BrawlerGadgetName> {

    /**
     * Compares this [BrawlerGadgetName] with [other] based on lexicographic order of the underlying [rawString].
     */
    override fun compareTo(other: BrawlerGadgetName): Int = rawString.compareTo(other.rawString)

    /**
     * Returns the string representation of the gadget name.
     */
    override fun toString(): String = rawString
}

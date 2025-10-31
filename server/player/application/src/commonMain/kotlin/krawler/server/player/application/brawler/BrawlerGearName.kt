package krawler.server.player.application.brawler

/**
 * Represents the name of a Gear in Brawl Stars.
 *
 * Gears provide passive abilities to brawlers, and each gear has a display name.
 * This value class wraps a [String] and enforces strong typing for gear names,
 * enabling safer APIs and better domain modeling.
 *
 * Instances of [BrawlerGearName] can be compared lexicographically.
 *
 * @property string The raw name string of the gear.
 */
@JvmInline
value class BrawlerGearName(
    /**
     * The raw name string of the gear.
     */
    val string: String,
) : Comparable<BrawlerGearName> {

    /**
     * Compares this [BrawlerGearName] with another [BrawlerGearName] based on lexicographic order
     * of their underlying [string] values.
     *
     * @param other The other gear name to compare to.
     * @return A negative integer, zero, or a positive integer as this name is
     * less than, equal to, or greater than the other.
     */
    override fun compareTo(other: BrawlerGearName): Int = string.compareTo(other.string)

    /**
     * Returns the string representation of the gear name.
     *
     * @return The raw name string.
     */
    override fun toString(): String = string
}

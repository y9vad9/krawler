package krawler.server.player.application

/**
 * Represents the number of brawlers a Brawl Stars player has.
 *
 * This value class wraps an integer count of brawlers.
 * The count is guaranteed to be ≥ 0.
 *
 * @property int The raw integer value of brawlers.
 */
@JvmInline
value class BrawlersAmount(
    val int: Int
) : Comparable<BrawlersAmount> {

    init {
        require(int >= 0) { "Brawlers amount must be zero or greater, got $int." }
    }

    /**
     * Compares this [BrawlersAmount] with another by numeric value.
     */
    override fun compareTo(other: BrawlersAmount): Int = int.compareTo(other.int)

    /**
     * Subtracts another [BrawlersAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [BrawlersAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: BrawlersAmount): BrawlersAmount =
        BrawlersAmount(this.int - other.int)

    override fun toString(): String = int.toString()
}

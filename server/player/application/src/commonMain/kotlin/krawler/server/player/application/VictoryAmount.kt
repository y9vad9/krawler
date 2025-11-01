package krawler.server.player.application

/**
 * Represents the number of victories a Brawl Stars player has achieved.
 *
 * Victories are accumulated through successful battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * @property int The raw integer value of victories (guaranteed to be ≥ 0).
 */
@JvmInline
value class VictoryAmount(
    val int: Int,
) : Comparable<VictoryAmount> {

    init {
        require(int >= 0) {
            "Victory count must be zero or greater."
        }
    }

    /**
     * Compares this [VictoryAmount] with another by numeric value.
     */
    override fun compareTo(other: VictoryAmount): Int =
        int.compareTo(other.int)

    /**
     * Adds another [VictoryAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [VictoryAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: VictoryAmount): VictoryAmount =
        VictoryAmount(int + other.int)

    /**
     * Subtracts another [VictoryAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [VictoryAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: VictoryAmount): VictoryAmount =
        VictoryAmount(int - other.int)

    /**
     * Returns the string representation of the victory count.
     */
    override fun toString(): String = int.toString()
}

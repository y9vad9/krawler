package krawler.server.player.application

/**
 * Represents the number of draws a Brawl Stars player has achieved.
 *
 * Draws are accumulated through battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * @property int The raw integer value of draws (guaranteed to be ≥ 0).
 */
@JvmInline
value class DrawAmount(
    val int: Int
) : Comparable<DrawAmount> {

    init {
        require(int >= 0) { "Draw amount must be zero or greater, got $int." }
    }

    /**
     * Compares this [DrawAmount] with another by numeric value.
     */
    override fun compareTo(other: DrawAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [DrawAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [DrawAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: DrawAmount): DrawAmount =
        DrawAmount(this.int + other.int)

    /**
     * Subtracts another [DrawAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [DrawAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: DrawAmount): DrawAmount =
        DrawAmount(this.int - other.int)

    /**
     * Returns the string representation of the draw count.
     */
    override fun toString(): String = int.toString()
}

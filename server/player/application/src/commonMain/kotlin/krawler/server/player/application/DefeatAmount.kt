package krawler.server.player.application

/**
 * Represents the number of defeats a Brawl Stars player has achieved.
 *
 * Defeats are accumulated through lost battles in various game modes.
 * This value class ensures the count is always non-negative.
 *
 * @property int The raw integer value of defeats (guaranteed to be ≥ 0).
 */
@JvmInline
value class DefeatAmount(
    val int: Int
) : Comparable<DefeatAmount> {

    init {
        require(int >= 0) { "Defeat amount must be zero or greater, got $int." }
    }

    /**
     * Compares this [DefeatAmount] with another by numeric value.
     */
    override fun compareTo(other: DefeatAmount): Int = int.compareTo(other.int)

    /**
     * Adds another [DefeatAmount] to this one.
     *
     * @param other The amount to add.
     * @return A new [DefeatAmount] representing the sum.
     * @throws IllegalArgumentException if the result is negative (overflow).
     */
    operator fun plus(other: DefeatAmount): DefeatAmount =
        DefeatAmount(this.int + other.int)

    /**
     * Subtracts another [DefeatAmount] from this one.
     *
     * @param other The amount to subtract.
     * @return A new [DefeatAmount] representing the difference.
     * @throws IllegalArgumentException if the result is negative.
     */
    operator fun minus(other: DefeatAmount): DefeatAmount =
        DefeatAmount(this.int - other.int)

    /**
     * Returns the string representation of the defeat count.
     */
    override fun toString(): String = int.toString()
}

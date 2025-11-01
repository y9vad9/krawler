package krawler.server.player.application

/**
 * Represents the number of trophies a Brawl Stars player has.
 *
 * Trophies are a core metric representing player progress and skill
 * in various game modes. This value class provides type safety and
 * comparability based on the integer trophy count.
 *
 * @property rawInt The raw trophy count.
 */
@JvmInline
value class Trophies(val rawInt: Int) : Comparable<Trophies> {

    init {
        require(rawInt >= MIN_VALUE) {
            "Trophy count must be at least $MIN_VALUE: $rawInt"
        }
    }

    override fun compareTo(other: Trophies): Int = rawInt.compareTo(other.rawInt)

    operator fun plus(other: Trophies): Trophies = Trophies(this.rawInt + other.rawInt)

    override fun toString(): String = rawInt.toString()

    companion object {
        /** Minimum valid trophy count. */
        const val MIN_VALUE: Int = 0
    }
}

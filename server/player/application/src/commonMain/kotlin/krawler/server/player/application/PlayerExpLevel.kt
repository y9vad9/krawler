package krawler.server.player.application

/**
 * Represents the account experience level of a Brawl Stars player.
 *
 * In Brawl Stars, experience levels (distinct from XP tokens) reflect account progression based
 * on the cumulative experience earned over time by playing matches, completing quests, earning
 * "Star Player" bonuses, and participating in events. Experience level milestones were historically
 * associated with rewards such as Tokens and Profile Icons.
 *
 * This value class wraps an integer representation of the experience level and allows comparison
 * between levels using natural order.
 *
 * Note: Experience levels do **not** affect matchmaking, brawler strength, or competitive ranking.
 *
 * [Learn more](https://brawlstars.fandom.com/wiki/Experience).
 *
 * @property int The numeric experience level.
 */
@JvmInline
value class PlayerExpLevel(
    val int: Int,
) : Comparable<PlayerExpLevel> {

    init {
        require(int >= 0) {
            "Player experience level must be zero or greater."
        }
    }

    /**
     * Compares this experience level to another.
     *
     * @param other The other [PlayerExpLevel] to compare against.
     * @return Negative if this < other, zero if equal, positive if this > other.
     */
    override fun compareTo(other: PlayerExpLevel): Int =
        int.compareTo(other.int)
}

package krawler.server.player.application

/**
 * Represents the experience points (XP) earned by a Brawl Stars player.
 *
 * Each match completed—regardless of outcome—contributes XP to a player's account,
 * which is used to advance account level and unlock cosmetic and token rewards.
 * Experience Points are capped daily and do **not** impact matchmaking or gameplay performance.
 *
 * Experience was later replaced by Mastery Points in February 2023 for individual brawler progression,
 * but account-level XP remains visible and relevant in player profiles.
 *
 * @property int The raw integer amount of XP earned.
 * @see <a href="https://brawlstars.fandom.com/wiki/Experience">Brawl Stars Experience - Fandom Wiki</a>
 */
@JvmInline
value class PlayerExpPoints(
    val int: Int
) : Comparable<PlayerExpPoints> {

    init {
        require(int >= 0) { "Player experience points must be zero or greater, but was $int." }
    }

    /**
     * Compares this [PlayerExpPoints] with another based on their numeric value.
     *
     * @param other The other XP value to compare against.
     * @return Negative if this < other, zero if equal, positive if this > other.
     */
    override fun compareTo(other: PlayerExpPoints): Int = int.compareTo(other.int)

    /**
     * Returns the string representation of this XP value.
     */
    override fun toString(): String = int.toString()
}

package krawler.server.player.application.battle

import kotlin.jvm.JvmInline

/**
 * Represents a Brawl Stars event mode.
 *
 * This class encapsulates the raw string identifier for a specific event mode in Brawl Stars.
 * Event modes define the rules and objectives of various game modes within the game.
 *
 * @property rawString The raw string identifier for the event mode.
 */
@JvmInline
value class BattleEventMode(val rawString: String) : Comparable<BattleEventMode> {
    /**
     * Compares this event mode to another event mode based on their raw string identifiers.
     *
     * @param other The other event mode to compare to.
     * @return A negative integer, zero, or a positive integer as this event mode is less than,
     *         equal to, or greater than the specified event mode.
     */
    override fun compareTo(other: BattleEventMode): Int = rawString.compareTo(other.rawString)
}

package krawler.server.player.application.battle

import kotlin.jvm.JvmInline

/**
 * Represents the name of a Brawl Stars map as a raw [String] value.
 *
 * This value is taken directly from the Brawl Stars API and reflects the localized
 * or internal name of the map associated with an event or mode.
 *
 * Note: The map name is not guaranteed to be unique across all time and modes.
 * It's primarily useful for display or identification in the context of a specific [BattleEvent].
 *
 * @property string The raw name of the map.
 */
@JvmInline
value class BattleMapName(val string: String)

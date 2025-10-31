package krawler.server.player.application.brawler

/**
 * Represents a Gadget in Brawl Stars.
 *
 * A Gadget is a special item that brawlers can equip to gain unique abilities
 * during battles. Each gadget is identified by a unique [id] and has a
 * human-readable [name].
 *
 * @property id The unique identifier of the gadget.
 * @property name The display name of the gadget.
 */
data class BrawlerGadget(
    /**
     * The unique identifier for this gadget.
     */
    val id: BrawlerGadgetId,

    /**
     * The name of this gadget, as shown in-game.
     */
    val name: BrawlerGadgetName,
)

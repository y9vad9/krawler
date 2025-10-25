package krawler.server.brawlstars.client.event

import kotlinx.serialization.Serializable

/**
 * Represents a scheduled in-game event with a specific time window and event details.
 *
 * @property startTime ISO 8601 UTC timestamp marking when the event starts.
 * @property endTime ISO 8601 UTC timestamp marking when the event ends.
 * @property slotId Unique identifier for the event slot, indicating its position in the event rotation.
 * @property event Detailed information about the event, including mode, map, and optional modifiers.
 */
@Serializable
data class RawScheduledEvent(
    val startTime: String,
    val endTime: String,
    val slotId: Int,
    val event: RawScheduledEventDetails,
)

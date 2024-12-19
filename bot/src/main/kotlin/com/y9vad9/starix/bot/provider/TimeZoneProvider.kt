package com.y9vad9.starix.bot.provider

import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.types.IdChatIdentifier
import java.time.ZoneId
import java.time.ZonedDateTime

interface TimeZoneProvider {
    companion object;

    suspend fun getTimeZone(id: IdChatIdentifier): ZoneId
    suspend fun setTimeZone(chatId: IdChatIdentifier, zoneId: ZoneId): Strings
}


fun ZoneId.toOffsetString(): String {
    val now = ZonedDateTime.now(this)
    return now.offset.id.replace("Z", "UTC+0")
}

/**
 * @return [Map] of [ZoneId] and [String] with human representative string (e.g: UTC-11: New Zealand, United States)
 */
fun TimeZoneProvider.Companion.getTimeZonesWithCountries(): Map<ZoneId, String> {
    val groupedByOffset = ZoneId.getAvailableZoneIds()
        .map { ZoneId.of(it) }
        .groupBy { zoneId ->
            val now = ZonedDateTime.now(zoneId)
            now.offset.id.replace("Z", "UTC+0").replace(":", "")
        }

    return groupedByOffset.flatMap { (offset, zones) ->
        val countries = zones.map { zone ->
            zone.id.split('/').drop(1).joinToString(", ") { it.replace('_', ' ') }
        }.joinToString(", ")

        zones.map { zoneId -> zoneId to "$offset: $countries" }
    }.toMap()
}


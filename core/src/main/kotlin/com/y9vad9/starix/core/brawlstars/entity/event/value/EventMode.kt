package com.y9vad9.starix.core.brawlstars.entity.event.value

import com.y9vad9.starix.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EventMode private constructor(private val value: String) : Comparable<EventMode> {
    companion object : ValueConstructor<EventMode, String> {
        override val displayName: String = "EventMode"

        val BRAWL_BALL: EventMode = EventMode("brawlBall")
        val SOLO_SHOWDOWN: EventMode = EventMode("soloShowdown")
        val DUO_SHOWDOWN: EventMode = EventMode("duoShowdown")
        val GEM_GRAB: EventMode = EventMode("gemGrab")
        val HOT_ZONE: EventMode = EventMode("hotZone")
        val DUELS: EventMode = EventMode("duels")
        val KNOCKOUT: EventMode = EventMode("knockout")
        val HEIST: EventMode = EventMode("heist")
        val BOUNTY: EventMode = EventMode("bounty")
        val SIEGE: EventMode = EventMode("siege")
        val BIG_GAME: EventMode = EventMode("bigGame")
        val BOSS_FIGHT: EventMode = EventMode("bossFight")
        val ROBO_RUMBLE: EventMode = EventMode("roboRumble")
        val TAKEDOWN: EventMode = EventMode("takedown")
        val LONE_STAR: EventMode = EventMode("loneStar")
        val PRESENT_PLUNDER: EventMode = EventMode("presentPlunder")
        val SUPER_CITY_RAMPAGE: EventMode = EventMode("superCityRampage")
        val VOLLEYBRAWL: EventMode = EventMode("volleyBrawl")
        val BASKET_BRAWL: EventMode = EventMode("basketBrawl")
        val HOLD_THE_TROPHY: EventMode = EventMode("holdTheTrophy")
        val TROPHY_THIEVES: EventMode = EventMode("trophyThieves")
        val WIPE_OUT: EventMode = EventMode("wipeout")
        val PAYLOAD: EventMode = EventMode("payload")
        val BOT_DROP: EventMode = EventMode("botDrop")
        val HUNTERS: EventMode = EventMode("hunters")
        val LAST_STAND: EventMode = EventMode("lastStand")
        val SNOWTEL_THIEVES: EventMode = EventMode("snowtelThieves")
        val PUMPKIN_PLUNDER: EventMode = EventMode("pumpkinPlunder")
        val TROPHY_ESCAPE: EventMode = EventMode("trophyEscape")
        val WIPE_OUT_5V5: EventMode = EventMode("wipeout5v5")
        val KNOCKOUT_5V5: EventMode = EventMode("knockout5v5")
        val GEM_GRAB_5V5: EventMode = EventMode("gemGrab5v5")
        val BRAWL_BALL_5V5: EventMode = EventMode("brawlBall5v5")
        val GODZILLA_CITY_SMASH: EventMode = EventMode("godzillaCitySmash")
        val PAINT_BRAWL: EventMode = EventMode("paintBrawl")
        val TRIO_SHOWDOWN: EventMode = EventMode("trioShowdown")
        val ZOMBIE_PLUNDER: EventMode = EventMode("zombiePlunder")
        val JELLY_FISHING: EventMode = EventMode("jellyfishing")
        val SPIRIT_WARS: EventMode = EventMode("spiritWars")
        val HOLIDAY_HAVOC: EventMode = EventMode("holidayHavoc")

        /**
         * This [EventMode] represents unknown yet for official Brawl Stars API event mode.
         */
        val UNKNOWN: EventMode = EventMode("unknown")

        override fun create(value: String): Result<EventMode> {
            return Result.success(EventMode(value))
        }
    }

    override fun compareTo(other: EventMode): Int {
        return value.lowercase().compareTo(other.value.lowercase())
    }
}
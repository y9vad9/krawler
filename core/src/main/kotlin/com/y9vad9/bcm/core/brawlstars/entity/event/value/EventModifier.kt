package com.y9vad9.bcm.core.brawlstars.entity.event.value

import com.y9vad9.bcm.foundation.validation.ValueConstructor
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EventModifier private constructor(val value: String) {
    companion object : ValueConstructor<EventModifier, String> {
        override val displayName: String = "EventModifier"

        val NONE: EventModifier = EventModifier("none")
        val ENERGY_DRINK: EventModifier = EventModifier("energyDrink")
        val ANGRY_ROBO: EventModifier = EventModifier("angryRobo")
        val METEOR_SHOWER: EventModifier = EventModifier("meteorShower")
        val GRAVEYARD_SHIFT: EventModifier = EventModifier("graveyardShift")
        val HEALING_MUSHROOMS: EventModifier = EventModifier("healingMushrooms")
        val BOSS_FIGHT_ROCKETS: EventModifier = EventModifier("bossFightRockets")
        val TAKEDOWN_LASERS: EventModifier = EventModifier("takedownLasers")
        val TAKEDOWN_CHAIN_LIGHTNING: EventModifier = EventModifier("takedownChainLightning")
        val TAKEDOWN_ROCKETS: EventModifier = EventModifier("takedownRockets")
        val WAVES: EventModifier = EventModifier("waves")
        val HAUNTED_BALL: EventModifier = EventModifier("hauntedBall")
        val SUPER_CHARGE: EventModifier = EventModifier("superCharge")
        val FAST_BRAWLERS: EventModifier = EventModifier("fastBrawlers")
        val SHOWDOWN_PLUS: EventModifier = EventModifier("showdown+")
        val PEEK_A_BOO: EventModifier = EventModifier("peekABoo")
        val BURNING_BALL: EventModifier = EventModifier("burningBall")

        override fun create(value: String): Result<EventModifier> {
            return Result.success(EventModifier(value))
        }
    }
}
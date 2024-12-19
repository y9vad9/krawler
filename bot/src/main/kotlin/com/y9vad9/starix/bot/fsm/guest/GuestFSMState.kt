package com.y9vad9.starix.bot.fsm.guest

import com.y9vad9.bcm.bot.fsm.FSMState
import kotlinx.serialization.Serializable

@Serializable
sealed interface GuestFSMState<D : FSMState.Dependencies> : FSMState<D>
package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.fsm.FSMState
import dev.inmo.micro_utils.fsm.common.State
import kotlinx.serialization.Serializable

@Serializable
sealed interface CommonFSMState<I : State, D : FSMState.Dependencies> : FSMState<I, D>

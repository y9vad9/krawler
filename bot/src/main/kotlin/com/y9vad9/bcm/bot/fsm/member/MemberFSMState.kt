package com.y9vad9.bcm.bot.fsm.member

import com.y9vad9.bcm.bot.fsm.FSMState
import dev.inmo.micro_utils.fsm.common.State
import kotlinx.serialization.Serializable

@Serializable
sealed interface MemberFSMState<I : O, O : State, D : FSMState.Dependencies> : FSMState<I, O, D>

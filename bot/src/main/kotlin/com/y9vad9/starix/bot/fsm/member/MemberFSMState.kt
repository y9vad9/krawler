package com.y9vad9.starix.bot.fsm.member

import com.y9vad9.starix.bot.fsm.FSMState
import kotlinx.serialization.Serializable

@Serializable
sealed interface MemberFSMState<D : FSMState.Dependencies> : FSMState<D>

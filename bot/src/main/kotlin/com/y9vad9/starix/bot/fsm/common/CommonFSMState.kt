package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.starix.bot.fsm.FSMState

interface CommonFSMState<D : FSMState.Dependencies> : FSMState<D>

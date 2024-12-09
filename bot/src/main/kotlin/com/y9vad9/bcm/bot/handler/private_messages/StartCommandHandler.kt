package com.y9vad9.bcm.bot.handler.private_messages

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import dev.inmo.tgbotapi.extensions.behaviour_builder.DefaultBehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand


suspend fun DefaultBehaviourContextWithFSM<FSMState<*, *, *>>.handleStartCommand() {
    onCommand("start") {
        startChain(CommonInitialState(it.chat.id))
    }
}
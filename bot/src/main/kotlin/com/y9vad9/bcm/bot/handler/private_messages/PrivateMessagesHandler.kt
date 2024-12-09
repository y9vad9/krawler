package com.y9vad9.bcm.bot.handler.private_messages

import com.y9vad9.bcm.bot.fsm.FSMState
import dev.inmo.tgbotapi.extensions.behaviour_builder.DefaultBehaviourContextWithFSM

interface PrivateMessagesHandler {
    suspend fun DefaultBehaviourContextWithFSM<FSMState>.handle()
}
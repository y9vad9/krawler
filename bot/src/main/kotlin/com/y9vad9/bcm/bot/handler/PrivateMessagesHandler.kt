package com.y9vad9.bcm.bot.handler

import com.y9vad9.bcm.bot.BotDependencies
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import dev.inmo.tgbotapi.extensions.behaviour_builder.DefaultBehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import kotlin.reflect.KClass

suspend fun DefaultBehaviourContextWithFSM<FSMState<*, *>>.handleBotPm(
    dependencies: BotDependencies,
    states: List<KClass<FSMState<*, FSMState.Dependencies>>>,
) {
    onCommand("start") {
        startChain(CommonInitialState(it.chat.id))
    }

    states.forEach {
        addStrict(it) {
            @Suppress("UNCHECKED_CAST")
            it as FSMState<Any, FSMState.Dependencies>

            with(it) {
                before(it, dependencies)?.also { result ->
                    if (it != result)
                        return@addStrict result as FSMState<*, *>?
                }

                process(dependencies, it)
            }
        }
    }
}
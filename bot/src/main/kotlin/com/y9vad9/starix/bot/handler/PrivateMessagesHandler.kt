package com.y9vad9.starix.bot.handler

import com.y9vad9.bcm.bot.BotDependencies
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import dev.inmo.tgbotapi.extensions.behaviour_builder.DefaultBehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import kotlin.reflect.KClass

suspend fun DefaultBehaviourContextWithFSM<FSMState<*>>.handleBotPm(
    dependencies: BotDependencies,
    states: List<KClass<FSMState<FSMState.Dependencies>>>,
) {
    onCommand("start") {
        startChain(CommonInitialState(it.chat.id))
    }

    states.forEach { kClass ->
        addStrict(kClass) { state ->
            println("initial: $state")
            @Suppress("UNCHECKED_CAST")
            state

            println("process")
            var currentState = with(state) {
                process(dependencies)
            }

            println("processed $currentState")

            if (currentState == state)
                return@addStrict currentState

            var previousState = state

            while (isActive) {
                @Suppress("UNCHECKED_CAST")
                currentState as FSMState<FSMState.Dependencies>
                val beforeResult = with(currentState) {
                    println("beforeResult scope $currentState")
                    before(previousState, dependencies)
                }.also { println("processed $it") }

                if (beforeResult != currentState) {
                    println("continue")
                    previousState = currentState
                    currentState = beforeResult
                    continue
                } else {
                    println("break $beforeResult")
                    return@addStrict beforeResult
                }
            }
            yield() // if cancellation exception
            error("Should not reach this point.")
        }
    }
}
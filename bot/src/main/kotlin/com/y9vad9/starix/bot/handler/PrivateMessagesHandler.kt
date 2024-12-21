package com.y9vad9.starix.bot.handler

import com.y9vad9.starix.bot.BotDependencies
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import dev.inmo.tgbotapi.extensions.behaviour_builder.DefaultBehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.*
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.sender_chat
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import kotlin.reflect.KClass

suspend fun DefaultBehaviourContextWithFSM<FSMState<*>>.handleBotPm(
    dependencies: BotDependencies,
    states: List<KClass<FSMState<FSMState.Dependencies>>>,
) {
    onCommand("start") {
        @OptIn(ValidationDelicateApi::class)
        it.chat.commonUserOrNull()?.languageCode?.let { language ->
            dependencies.stringsProvider.setStrings(
                chatId = it.chat.id,
                code = LanguageCode.createUnsafe(language),
            )
        }
        startChain(CommonInitialState(it.chat.id))
    }

    states.forEach { kClass ->
        addStrict(kClass) { state ->

            var currentState = with(state) {
                process(dependencies)
            }

            if (currentState == state)
                return@addStrict currentState

            var previousState = state

            while (isActive) {
                @Suppress("UNCHECKED_CAST")
                currentState as FSMState<FSMState.Dependencies>
                val beforeResult = with(currentState) {
                    before(previousState, dependencies)
                }

                if (beforeResult != currentState) {
                    previousState = currentState
                    currentState = beforeResult
                    continue
                } else {
                    return@addStrict beforeResult
                }
            }
            yield() // if cancellation exception
            error("Should not reach this point.")
        }
    }
}
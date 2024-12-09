package com.y9vad9.bcm.bot.fsm

import com.y9vad9.bcm.localization.Strings
import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.LogLevel
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.IdChatIdentifier

interface FSMState<I : O, O : State, D : FSMState.Dependencies> : State {
    abstract override val context: IdChatIdentifier

    suspend fun BehaviourContext.before(previousState: FSMState<*, *, *>, dependencies: D): O?
    suspend fun BehaviourContextWithFSM<in O>.process(dependencies: D, state: I): O?

    interface Dependencies {
        val bot: TelegramBot
        val logger: KSLog
        val strings: Strings
    }
}

internal fun FSMState<*, *, *>.createLoggingMessage(
    logger: KSLog,
    throwable: Throwable,
) {
    logger.performLog(
        level = LogLevel.ERROR,
        tag = "${this::class.simpleName}",
        message = "Exception occurred, state information: $this",
        throwable = throwable,
    )
}
package com.y9vad9.starix.bot.fsm

import com.y9vad9.starix.bot.provider.StringsProvider
import com.y9vad9.starix.bot.provider.TimeZoneProvider
import com.y9vad9.starix.localization.Strings
import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.LogLevel
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.IdChatIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext
import kotlin.uuid.Uuid

interface FSMState<D : FSMState.Dependencies> : State {
    override val context: IdChatIdentifier

    suspend fun BehaviourContext.before(previousState: FSMState<*>, dependencies: D): FSMState<*>?
    suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(dependencies: D): FSMState<*>?

    interface Dependencies {
        val bot: TelegramBot
        val logger: KSLog
        val stringsProvider: StringsProvider
        val timeZoneProvider: TimeZoneProvider
        val globalScope: CoroutineScope
    }
}

internal suspend fun FSMState.Dependencies.logAndProvideMessage(
    state: FSMState<*>,
    throwable: Throwable,
) {
    val uuid = Uuid.random()
    logger.performLog(
        level = LogLevel.ERROR,
        tag = uuid.toString(),
        message = state.toString() + ": " + throwable.stackTraceToString(),
        throwable = throwable,
    )

    bot.send(
        chatId = state.context,
        entities = strings.failureToMessageWithUuid(uuid, throwable),
    )
}

suspend fun FSMState.Dependencies.getCurrentStrings(chatIdentifier: IdChatIdentifier) =
    stringsProvider.getStrings(chatIdentifier)
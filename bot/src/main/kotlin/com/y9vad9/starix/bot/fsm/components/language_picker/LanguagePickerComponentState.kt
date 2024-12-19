package com.y9vad9.starix.bot.fsm.components.language_picker

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.system.entity.value.LanguageCode
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("LanguagePickerComponentState")
@Serializable
data class LanguagePickerComponentState(
    override val context: IdChatIdentifier,
    val callback: Callback,
) : FSMState<FSMState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: FSMState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = stringsProvider.getStrings(context)

        bot.send(
            chatId = context,
            entities = strings.components.languagePicker.chooseLanguageMessage,
            replyMarkup = replyKeyboard {
                stringsProvider.getAvailableStrings().chunked(3).forEach { list ->
                    row {
                        list.forEach { strings ->
                            simpleButton(strings.displayName)
                        }
                    }
                }
            }
        )
        this@LanguagePickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: FSMState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val input = waitText().first().text) {
            strings.goBackChoice -> callback.navigateBack(context)
            else -> {
                val pickedLanguage = stringsProvider.getAvailableStrings()
                    .firstOrNull { it.displayName == input }
                    ?.languageCode
                    ?: run {
                        bot.send(
                            chatId = context,
                            text = strings.invalidChoiceMessage,
                        )
                        return@with this@LanguagePickerComponentState
                    }

                callback.navigateForward(context, pickedLanguage)
            }
        }
    }

    interface Callback {
        fun navigateBack(context: IdChatIdentifier): FSMState<*>
        fun navigateForward(context: IdChatIdentifier, code: LanguageCode): FSMState<*>
    }
}
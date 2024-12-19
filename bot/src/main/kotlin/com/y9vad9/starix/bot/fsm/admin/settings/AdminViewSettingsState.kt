package com.y9vad9.starix.bot.fsm.admin.settings

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminViewSettingsState")
@Serializable
data class AdminViewSettingsState(override val context: IdChatIdentifier) :
    FSMState<AdminViewSettingsState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.viewSettingsBeginMessage,
            replyMarkup = replyKeyboard {
                row {
                    simpleButton(strings.admin.clubSettingsOption)
                    simpleButton(strings.admin.manageMembersOption)
                }
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@AdminViewSettingsState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (waitText().first().text) {
            strings.admin.clubSettingsOption -> TODO()
            strings.admin.manageMembersOption -> TODO()
        }
        TODO()
    }

    interface Dependencies : FSMState.Dependencies
}
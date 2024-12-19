package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.common.entity.value.Link
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.inline.urlInlineButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("CommonChatRulesState")
@Serializable
data class CommonChatRulesState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val inviteLink: Link,
) : CommonFSMState<CommonChatRulesState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val chatRules = settingsRepository.getSettings()
            .allowedClubs[clubTag]!!
            .chatRules.value

        bot.send(
            chatId = context,
            entities = strings.guest.chatRules(chatRules),
            replyMarkup = replyKeyboard {
                row(simpleReplyButton(strings.guest.acceptRulesChoice))
                row(simpleReplyButton(strings.goBackChoice))
            }
        )

        this@CommonChatRulesState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val reply = waitText().first().text
        when (reply) {
            strings.goBackChoice -> {
                CommonInitialState(context)
            }

            strings.guest.acceptRulesChoice -> {
                bot.send(
                    chatId = context,
                    entities = strings.guest.joinClubChatMessage,
                    replyMarkup = inlineKeyboard {
                        add(listOf(urlInlineButton(strings.joinChatButton, inviteLink.value)))
                    },
                )
                CommonInitialState(context)
            }

            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@CommonChatRulesState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val settingsRepository: SettingsRepository
    }
}
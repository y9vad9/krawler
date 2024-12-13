package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.member.MemberMainMenuState
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.common.entity.value.Link
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class CommonChatRulesState private constructor(
    override val context: IdChatIdentifier,
    val clubTag: String,
    val inviteLink: String,
) : CommonFSMState<CommonChatRulesState, CommonChatRulesState.Dependencies> {
    constructor(
        context: IdChatIdentifier,
        clubTag: ClubTag,
        inviteLink: Link,
    ) : this(context, clubTag.toString(), inviteLink.value)

    @OptIn(ValidationDelicateApi::class)
    val clubTagWrapped by lazy { ClubTag.createUnsafe(clubTag) }

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *>,
        dependencies: Dependencies,
    ): FSMState<*, *>? = with(dependencies) {
        @OptIn(ValidationDelicateApi::class)
        val chatRules = settingsRepository.getSettings()
            .allowedClubs[clubTagWrapped]!!
            .chatRules.value

        bot.send(
            chatId = context,
            text = strings.chatRules(chatRules),
            replyMarkup = replyKeyboard {
                add(listOf(SimpleKeyboardButton(strings.acceptRulesChoice)))
                add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
            }
        )

        this@CommonChatRulesState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*, *>>.process(
        dependencies: Dependencies,
        state: CommonChatRulesState,
    ): FSMState<*, *>? = with(dependencies) {
        val reply = waitText().first().text
        when (reply) {
            strings.goBackChoice -> {
                CommonInitialState(context)
            }

            strings.acceptRulesChoice -> {
                bot.send(
                    chatId = context,
                    text = strings.joinClubChatMessage(inviteLink),
                    replyMarkup = ReplyKeyboardRemove(),
                )
                MemberMainMenuState(context)
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
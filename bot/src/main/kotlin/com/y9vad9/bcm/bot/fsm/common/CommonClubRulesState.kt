package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonChatRulesState
import com.y9vad9.bcm.domain.entity.brawlstars.value.ClubTag
import com.y9vad9.bcm.domain.entity.value.Link
import com.y9vad9.bcm.domain.repository.SettingsRepository
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
class CommonClubRulesState private constructor(
    override val context: IdChatIdentifier,
    val clubTag: String,
    val inviteLink: String,
) : CommonFSMState<CommonClubRulesState, State, CommonClubRulesState.Dependencies> {
    constructor(context: IdChatIdentifier, clubTag: ClubTag, link: Link) : this(context, clubTag.toString(), link.value)

    @OptIn(ValidationDelicateApi::class)
    val clubTagWrapped by lazy { ClubTag.createUnsafe(clubTag) }

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *, *>,
        dependencies: Dependencies,
    ): State? = with(dependencies) {
        @OptIn(ValidationDelicateApi::class)
        val clubRules = settingsRepository.getSettings()
            .allowedClubs[clubTagWrapped]!!
            .clubRules.value

        bot.send(
            chatId = context,
            text = strings.clubRules(clubRules),
            replyMarkup = replyKeyboard {
                add(listOf(SimpleKeyboardButton(strings.acceptRulesChoice)))
                add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
            }
        )
        this@CommonClubRulesState
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun BehaviourContextWithFSM<in State>.process(
        dependencies: Dependencies,
        state: CommonClubRulesState,
    ): State? = with(dependencies) {
        val reply = waitText().first().text
        when (reply) {
            strings.goBackChoice -> {
                CommonInitialState(context)
            }
            strings.acceptRulesChoice -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                    replyMarkup = ReplyKeyboardRemove(),
                )
                CommonChatRulesState(context, clubTagWrapped, Link.createUnsafe(inviteLink))
            }
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                    replyMarkup = ReplyKeyboardRemove(),
                )
                this@CommonClubRulesState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val settingsRepository: SettingsRepository
    }
}
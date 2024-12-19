package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.common.entity.value.Link
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("CommonClubRulesState")
@Serializable
class CommonClubRulesState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val inviteLink: Link,
) : CommonFSMState<CommonClubRulesState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val clubRules = settingsRepository.getSettings()
            .allowedClubs[clubTag]!!
            .clubRules.value

        bot.send(
            chatId = context,
            entities = strings.guest.clubRules(clubRules),
            replyMarkup = replyKeyboard {
                row(simpleReplyButton(strings.guest.acceptRulesChoice))
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@CommonClubRulesState
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (waitText().first().text) {
            strings.goBackChoice -> {
                CommonInitialState(context)
            }

            strings.guest.acceptRulesChoice -> {
                CommonChatRulesState(context, clubTag, inviteLink)
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
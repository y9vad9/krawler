package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.chat_rules

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminViewChatRulesSettingState")
@Serializable
data class AdminViewChatRulesSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminViewChatRulesSettingState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val result = getClubSettings.execute(clubTag)) {
            GetClubSettingsUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }

            is GetClubSettingsUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    entities = strings.admin.settings.chatRulesMessage(result.clubSettings),
                    replyMarkup = replyKeyboard {
                        add(listOf(SimpleKeyboardButton(strings.changeOption)))
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    },
                )
                this@AdminViewChatRulesSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (waitText().first().text) {
            strings.changeOption -> AdminChangeChatRulesSettingState(context, clubTag)
            strings.goBackChoice -> AdminViewClubSettingsState(context, clubTag)
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminViewChatRulesSettingState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubSettings: GetClubSettingsUseCase
    }
}
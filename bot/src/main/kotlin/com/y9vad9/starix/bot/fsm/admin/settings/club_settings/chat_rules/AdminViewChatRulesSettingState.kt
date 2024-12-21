package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.chat_rules

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.components.language_picker.LanguagePickerComponentState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminViewChatRulesSettingState")
@Serializable
data class AdminViewChatRulesSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val languageCode: LanguageCode? = null,
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
                val locale = languageCode ?: result.clubSettings.defaultLanguage

                bot.send(
                    chatId = context,
                    entities = strings.admin.settings.chatRulesMessage(result.clubSettings.chatRules[locale], locale),
                    replyMarkup = replyKeyboard {
                        row(simpleReplyButton(strings.changeOption))
                        row(simpleReplyButton(strings.admin.settings.forAnotherLocaleChoice))
                        row(simpleReplyButton(strings.goBackChoice))
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
            strings.changeOption -> AdminChangeChatRulesSettingState(context, clubTag, languageCode)
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

    @SerialName("LanguagePickerToChatRulesCallback")
    @Serializable
    private data class LanguagePickerToChatRulesCallback(
        val clubTag: ClubTag,
    ) : LanguagePickerComponentState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminViewChatRulesSettingState(context, clubTag)
        }

        override fun navigateForward(context: IdChatIdentifier, code: LanguageCode): FSMState<*> {
            return AdminViewChatRulesSettingState(context, clubTag, code)
        }
    }
}
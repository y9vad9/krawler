package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.club_rules

import com.y9vad9.starix.bot.ext.asTelegramUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.common.entity.value.CustomMessage
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import com.y9vad9.starix.core.system.usecase.settings.admin.club.ChangeClubContactsInfoSettingUseCase
import com.y9vad9.starix.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminChangeClubRulesSettingState")
@Serializable
data class AdminChangeContactInfoSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val languageCode: LanguageCode? = null,
) : FSMState<AdminChangeContactInfoSettingState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (getClubSettings.execute(clubTag)) {
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
                    entities = strings.admin.settings.changeClubRulesMessage,
                    replyMarkup = replyKeyboard {
                        row(simpleReplyButton(strings.goBackChoice))
                    },
                )
                this@AdminChangeContactInfoSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val text = waitText().first().text) {
            strings.goBackChoice -> AdminViewContactInfoSettingState(context, clubTag)
            else -> {
                val message = CustomMessage.create(text)
                    .getOrElse {
                        bot.send(
                            chatId = context,
                            text = strings.constructor.customMessageFailure,
                        )
                        return@with this@AdminChangeContactInfoSettingState
                    }

                setContactsInfo(message)
            }
        }
    }

    private suspend fun Dependencies.setContactsInfo(
        message: CustomMessage,
    ): FSMState<*> {
        val strings = getCurrentStrings(context)

        return when (changeClubContactsInfoSetting.execute(context.asTelegramUserId(), clubTag, message, languageCode)) {
            ChangeClubContactsInfoSettingUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }

            ChangeClubContactsInfoSettingUseCase.Result.NoPermission -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }

            ChangeClubContactsInfoSettingUseCase.Result.ShouldNotBeEmpty -> {
                bot.send(
                    chatId = context,
                    text = strings.admin.settings.contactsInfoCannotBeEmpty,
                )
                AdminMainMenuState(context)
            }

            ChangeClubContactsInfoSettingUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    text = strings.admin.settings.successfullyChangedOption,
                )
                AdminViewClubSettingsState(context, clubTag)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubSettings: GetClubSettingsUseCase
        val changeClubContactsInfoSetting: ChangeClubContactsInfoSettingUseCase
    }
}
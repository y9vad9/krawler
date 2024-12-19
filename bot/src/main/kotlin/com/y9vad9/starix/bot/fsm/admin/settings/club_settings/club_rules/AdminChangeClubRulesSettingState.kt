package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.club_rules

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.common.entity.value.CustomMessage
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.ChangeClubRulesSettingUseCase
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

@SerialName("AdminChangeClubRulesSettingState")
@Serializable
data class AdminChangeClubRulesSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminChangeClubRulesSettingState.Dependencies> {
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
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    },
                )
                this@AdminChangeClubRulesSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val text = waitText().first().text) {
            strings.goBackChoice -> AdminViewClubRulesSettingState(context, clubTag)
            else -> {
                val message = CustomMessage.create(text)
                    .getOrElse {
                        bot.send(
                            chatId = context,
                            text = strings.constructor.customMessageFailure,
                        )
                        return@with this@AdminChangeClubRulesSettingState
                    }

                setClubRules(message)
            }
        }
    }

    private suspend fun Dependencies.setClubRules(
        message: CustomMessage,
    ): FSMState<*> {
        val strings = getCurrentStrings(context)

        return when (changeChatRulesSetting.execute(context.asTelegramUserId(), clubTag, message)) {
            ChangeClubRulesSettingUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }

            ChangeClubRulesSettingUseCase.Result.NoPermission -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }

            ChangeClubRulesSettingUseCase.Result.ShouldNotBeEmpty -> {
                bot.send(
                    chatId = context,
                    text = strings.admin.settings.rulesCannotBeEmptyMessage,
                )
                AdminMainMenuState(context)
            }

            ChangeClubRulesSettingUseCase.Result.Success -> {
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
        val changeChatRulesSetting: ChangeClubRulesSettingUseCase
    }
}
@file:Suppress("DuplicatedCode")

package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.join_request

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.ChangeClubJoinRequestsSettingUseCase
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

@SerialName("AdminManageJoinRequestSettingState")
@Serializable
data class AdminManageJoinRequestSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminManageJoinRequestSettingState.Dependencies> {
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
                    entities = strings.admin.settings.joinRequestsMessage(result.clubSettings),
                    replyMarkup = replyKeyboard {
                        val actionButtonText = if (result.clubSettings.joinViaBotRequest) {
                            strings.disableChoice
                        } else {
                            strings.enableChoice
                        }
                        add(listOf(SimpleKeyboardButton(actionButtonText)))
                        add(listOf(SimpleKeyboardButton(strings.admin.settings.joinRequestsRequirementsChoice)))
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    },
                )
                this@AdminManageJoinRequestSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val text = waitText().first().text) {
            strings.enableChoice, strings.disableChoice -> {
                val result = changeClubJoinRequestsSetting.execute(
                    id = context.asTelegramUserId(),
                    clubTag = clubTag,
                    status = text == strings.enableChoice,
                )

                when (result) {
                    ChangeClubJoinRequestsSettingUseCase.Result.ClubNotFound -> {
                        bot.send(
                            chatId = context,
                            text = strings.clubNotFoundMessage,
                        )
                        return@with AdminMainMenuState(context)
                    }

                    ChangeClubJoinRequestsSettingUseCase.Result.NoPermission -> {
                        bot.send(
                            chatId = context,
                            text = strings.noPermissionMessage,
                        )
                        return@with CommonInitialState(context)
                    }

                    ChangeClubJoinRequestsSettingUseCase.Result.Success -> {
                        bot.send(
                            chatId = context,
                            text = strings.admin.settings.successfullyChangedOption,
                        )
                        AdminViewClubSettingsState(context, clubTag)
                    }
                }
            }

            strings.admin.settings.joinRequestsRequirementsChoice ->
                AdminManageJoinRequestWithoutRequirementsSettingState(context, clubTag)

            strings.goBackChoice -> AdminViewClubSettingsState(context, clubTag)

            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminManageJoinRequestSettingState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubSettings: GetClubSettingsUseCase
        val changeClubJoinRequestsSetting: ChangeClubJoinRequestsSettingUseCase
    }
}
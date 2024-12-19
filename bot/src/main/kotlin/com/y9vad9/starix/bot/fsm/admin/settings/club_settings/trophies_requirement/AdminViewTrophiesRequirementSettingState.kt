package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.trophies_requirement

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
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

@SerialName("AdminViewTrophiesRequirementSettingState")
@Serializable
data class AdminViewTrophiesRequirementSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminViewTrophiesRequirementSettingState.Dependencies> {
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
                    entities = strings.admin.settings.trophieRequiremensMessage(result.clubSettings),
                    replyMarkup = replyKeyboard {
                        add(listOf(SimpleKeyboardButton(strings.changeOption)))
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    },
                )
                this@AdminViewTrophiesRequirementSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (waitText().first().text) {
            strings.changeOption -> AdminChangeTrophiesRequirementSettingState(context, clubTag)
            strings.goBackChoice -> AdminViewClubSettingsState(context, clubTag)
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminViewTrophiesRequirementSettingState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubSettings: GetClubSettingsUseCase
    }
}
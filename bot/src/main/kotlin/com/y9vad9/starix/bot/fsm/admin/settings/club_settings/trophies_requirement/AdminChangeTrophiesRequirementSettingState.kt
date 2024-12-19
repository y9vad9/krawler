package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.trophies_requirement

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.entity.event.value.Trophies
import com.y9vad9.bcm.core.brawlstars.entity.event.value.isPositive
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.ChangeClubMonthlyTrophiesRequirementSettingUseCase
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
import com.y9vad9.starix.foundation.validation.createOrNull
import com.y9vad9.starix.localization.Strings
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

@SerialName("AdminChangeTrophiesRequirementSettingState")
@Serializable
data class AdminChangeTrophiesRequirementSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminChangeTrophiesRequirementSettingState.Dependencies> {
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
                this@AdminChangeTrophiesRequirementSettingState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val text = waitText().first().text) {
            strings.goBackChoice -> AdminViewTrophiesRequirementSettingState(context, clubTag)
            else -> {
                val trophies: Trophies? = text.toIntOrNull()?.let { number ->
                    Trophies.createOrNull(number)?.takeIf { it.isPositive }
                }

                if (trophies == null) {
                    bot.send(
                        chatId = context,
                        text = strings.constructor.positiveTrophiesFailure,
                    )
                    return@with this@AdminChangeTrophiesRequirementSettingState
                }

                setChatRules(trophies, strings)
            }
        }
    }

    private suspend fun Dependencies.setChatRules(
        trophies: Trophies,
        strings: Strings,
    ): FSMState<*> {
        return when (changeClubMonthlyTrophiesRequirementSetting.execute(
            context.asTelegramUserId(),
            clubTag,
            trophies,
        )) {
            ChangeClubMonthlyTrophiesRequirementSettingUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }

            ChangeClubMonthlyTrophiesRequirementSettingUseCase.Result.NoPermission -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }

            ChangeClubMonthlyTrophiesRequirementSettingUseCase.Result.CannotBeNegative -> {
                bot.send(
                    chatId = context,
                    text = strings.constructor.positiveTrophiesFailure,
                )
                AdminMainMenuState(context)
            }

            ChangeClubMonthlyTrophiesRequirementSettingUseCase.Result.Success -> {
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
        val changeClubMonthlyTrophiesRequirementSetting: ChangeClubMonthlyTrophiesRequirementSettingUseCase
    }
}
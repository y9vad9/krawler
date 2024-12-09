package com.y9vad9.bcm.bot.fsm.guest

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.bcm.domain.repository.SettingsRepository
import com.y9vad9.bcm.domain.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.bcm.domain.usecase.GetAllowedClubsUseCase
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
data class GuestMainMenuState(
    override val context: IdChatIdentifier,
) : GuestFSMState<GuestMainMenuState, State, GuestMainMenuState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *, *>,
        dependencies: Dependencies,
    ): State? = with(dependencies) {
        when (val result = getAllowedClubs.execute()) {
            is GetAllowedClubsUseCase.Result.Failure -> {
                result.error.printStackTrace()
                bot.send(
                    chatId = previousState.context,
                    text = strings.failureToMessage(result.error),
                    replyMarkup = ReplyKeyboardRemove(),
                )
            }

            is GetAllowedClubsUseCase.Result.Success -> {
                bot.send(
                    chatId = previousState.context,
                    text = strings.guestStartMessage(result.clubs),
                    replyMarkup = replyKeyboard {
                        add(listOf(SimpleKeyboardButton(strings.hereToLinkChoice)))
                        add(listOf(SimpleKeyboardButton(strings.herePlanToJoinChoice)))
                        add(
                            listOf(
                                SimpleKeyboardButton(strings.viewContactPersonsChoice),
                                SimpleKeyboardButton(strings.viewGitHubRepositoryChoice),
                            )
                        )
                    }
                )
            }
        }

        this@GuestMainMenuState
    }

    override suspend fun BehaviourContextWithFSM<in State>.process(
        dependencies: Dependencies,
        state: GuestMainMenuState,
    ): State? = with(dependencies) {
        return when (waitText().first().text) {
            strings.herePlanToJoinChoice -> {
                // todo saved choices
                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = CommonPromptPlayerTagState.Purpose.EXPLORE_CLUBS
                )
            }

            strings.hereToLinkChoice -> {
                // todo saved choices
                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = CommonPromptPlayerTagState.Purpose.GUEST_LINK
                )
            }

            strings.viewContactPersonsChoice -> {
                bot.send(
                    chatId = context,
                    text = strings.guestShowContactsMessage(settingsRepository.getSettings()),
                )
                this@GuestMainMenuState
            }

            strings.viewGitHubRepositoryChoice -> {
                bot.send(
                    chatId = context,
                    text = strings.gitHubSourcesMessage,
                )
                this@GuestMainMenuState
            }

            else -> {
                bot.send(context, strings.invalidChoiceMessage)
                this@GuestMainMenuState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getAllowedClubs: GetAllowedClubsUseCase
        val checkClubsAvailability: CheckClubsAvailabilityUseCase
        val settingsRepository: SettingsRepository
    }
}
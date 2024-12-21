package com.y9vad9.starix.bot.fsm.guest

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.starix.bot.fsm.common.CommonSettingsMenuState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.starix.core.system.entity.en
import com.y9vad9.starix.core.system.entity.ru
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.system.usecase.GetAllowedClubsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("GuestMainMenuState")
@Serializable
data class GuestMainMenuState(
    override val context: IdChatIdentifier,
) : GuestFSMState<GuestMainMenuState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val result = getAllowedClubs.execute()) {
            is GetAllowedClubsUseCase.Result.Failure -> {
                logAndProvideMessage(
                    state = this@GuestMainMenuState,
                    throwable = result.error,
                )
            }

            is GetAllowedClubsUseCase.Result.Success -> {
                bot.send(
                    chatId = previousState.context,
                    entities = strings.guest.guestStartMessage(result.list),
                    replyMarkup = replyKeyboard {
                        row(simpleReplyButton(strings.guest.hereToLinkChoice))
                        row(simpleReplyButton(strings.guest.herePlanToJoinChoice))
                        row {
                            simpleButton(strings.guest.viewContactPersonsChoice)
                            simpleButton(strings.guest.viewGitHubRepositoryChoice)
                        }
                        row(simpleReplyButton(strings.generalSettingsOption))
                    },
                    linkPreviewOptions = LinkPreviewOptions.Disabled,
                )
            }
        }

        this@GuestMainMenuState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (waitText().first().text) {
            strings.guest.herePlanToJoinChoice -> {
                // todo saved choices
                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = CommonPromptPlayerTagState.Purpose.EXPLORE_CLUBS
                )
            }

            strings.guest.hereToLinkChoice -> {
                // todo saved choices
                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = CommonPromptPlayerTagState.Purpose.JOIN_CHAT
                )
            }

            strings.guest.viewContactPersonsChoice -> {
                bot.send(
                    chatId = context,
                    // todo
                    text = settingsRepository.getSettings().contactsInfo?.en?.value ?: "Not specified.",
                    linkPreviewOptions = LinkPreviewOptions.Disabled,
                )
                this@GuestMainMenuState
            }

            strings.generalSettingsOption -> CommonSettingsMenuState(
                context = context,
                callback = SettingsToGuestMenuCallback,
            )

            "/start" -> CommonInitialState(context)

            strings.guest.viewGitHubRepositoryChoice -> {
                bot.send(
                    chatId = context,
                    entities = strings.guest.gitHubSourcesMessage,
                    linkPreviewOptions = LinkPreviewOptions.Disabled,
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

    @SerialName("SettingsToGuestMenuCallback")
    @Serializable
    private data object SettingsToGuestMenuCallback : CommonSettingsMenuState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return GuestMainMenuState(context)
        }
    }
}
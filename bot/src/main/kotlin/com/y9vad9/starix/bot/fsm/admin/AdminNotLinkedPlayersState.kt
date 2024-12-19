package com.y9vad9.starix.bot.fsm.admin

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.usecase.GetNotLinkedBrawlStarsPlayersUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.utils.row
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminNotLinkedPlayersState")
@Serializable
class AdminNotLinkedPlayersState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : AdminFSMState<AdminNotLinkedPlayersState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            text = strings.admin.youAreInAdminMenuMessage,
            replyMarkup = replyKeyboard {
                row {
                    simpleButton(strings.admin.showNonLinkedPlayersChoice)
                    simpleButton(strings.admin.sendMessageChoice)
                }
            }
        )
        this@AdminNotLinkedPlayersState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val result = getNotLinkedBrawlStarsPlayers.execute(clubTag)) {
            GetNotLinkedBrawlStarsPlayersUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                return AdminMainMenuState(context)
            }

            is GetNotLinkedBrawlStarsPlayersUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminNotLinkedPlayersState, result.error)
            }

            is GetNotLinkedBrawlStarsPlayersUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    entities = strings.admin.nonLinkedPlayersMessage(result.list),
                    linkPreviewOptions = LinkPreviewOptions.Disabled
                )
            }
        }
        AdminMainMenuState(context)
    }

    interface Dependencies : FSMState.Dependencies {
        val getNotLinkedBrawlStarsPlayers: GetNotLinkedBrawlStarsPlayersUseCase
    }
}
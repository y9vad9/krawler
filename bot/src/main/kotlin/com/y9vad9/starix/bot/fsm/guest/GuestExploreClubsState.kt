package com.y9vad9.starix.bot.fsm.guest

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.brawlstars.usecase.CheckClubsAvailabilityUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("GuestExploreClubsState")
@Serializable
class GuestExploreClubsState(
    override val context: IdChatIdentifier,
    val chosenTag: PlayerTag,
) : GuestFSMState<GuestExploreClubsState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return@with when (val result = checkClubsAvailability.execute(chosenTag)) {
            is CheckClubsAvailabilityUseCase.Result.Failure -> {
                logAndProvideMessage(this@GuestExploreClubsState, result.error)
                GuestMainMenuState(context)
            }

            CheckClubsAvailabilityUseCase.Result.NoPlayerFound -> {
                bot.send(
                    chatId = context,
                    text = strings.playerTagNotFoundMessage,
                    replyMarkup = ReplyKeyboardRemove(),
                )

                // TODO provide savedList
                CommonPromptPlayerTagState(context, emptyList(), CommonPromptPlayerTagState.Purpose.EXPLORE_CLUBS)
            }

            is CheckClubsAvailabilityUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    entities = strings.guest.guestWantToJoinClubMessage(result.abilities),
                    replyMarkup = replyKeyboard {
                        row(simpleReplyButton(strings.goBackChoice))
                    }
                )
                this@GuestExploreClubsState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val reply = waitText().first().text
        return when (reply) {
            strings.goBackChoice -> GuestMainMenuState(context)
            else -> {
                bot.send(context, strings.invalidChoiceMessage)
                return@with this@GuestExploreClubsState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val checkClubsAvailability: CheckClubsAvailabilityUseCase
    }
}
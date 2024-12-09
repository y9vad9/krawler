package com.y9vad9.bcm.bot.fsm.guest

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.bcm.bot.fsm.createLoggingMessage
import com.y9vad9.bcm.domain.entity.brawlstars.value.PlayerTag
import com.y9vad9.bcm.domain.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
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
class GuestExploreClubsState private constructor(
    override val context: IdChatIdentifier,
    val chosenTag: String
) : GuestFSMState<GuestExploreClubsState, State, GuestExploreClubsState.Dependencies> {

    constructor(context: IdChatIdentifier, chosenTag: PlayerTag) : this(context, chosenTag.toString())

    @OptIn(ValidationDelicateApi::class)
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *, *>,
        dependencies: Dependencies,
    ): State? = with(dependencies) {
        return@with when (val result = checkClubsAvailability.execute(PlayerTag.createUnsafe(chosenTag))) {
            is CheckClubsAvailabilityUseCase.Result.Failure -> {
                createLoggingMessage(logger, result.error)
                bot.send(
                    chatId = context,
                    text = strings.failureToMessage(result.error)
                )
                GuestMainMenuState(context)
            }
            CheckClubsAvailabilityUseCase.Result.NoPlayerFound -> {
                bot.send(
                    chatId = context,
                    text = strings.playerNotFoundMessage,
                    replyMarkup = ReplyKeyboardRemove(),
                )

                // TODO provide savedList
                CommonPromptPlayerTagState(context, emptyList(), CommonPromptPlayerTagState.Purpose.EXPLORE_CLUBS)
            }
            is CheckClubsAvailabilityUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    text = strings.guestWantToJoinClubMessage(result.abilities),
                    replyMarkup = replyKeyboard {
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    }
                )
                this@GuestExploreClubsState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in State>.process(
        dependencies: Dependencies,
        state: GuestExploreClubsState,
    ): State? = with(dependencies) {
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
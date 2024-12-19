package com.y9vad9.starix.bot.fsm.admin

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.bcm.core.system.usecase.GetAllowedClubsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminChooseClubState")
@Serializable
data class AdminChooseClubState(
    override val context: IdChatIdentifier,
    val callback: Callback,
) : AdminFSMState<AdminChooseClubState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val result = getAllowedClubs.execute()) {
            is GetAllowedClubsUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminChooseClubState, throwable = result.error)
                this@AdminChooseClubState
            }

            is GetAllowedClubsUseCase.Result.Success -> {
                // when there's only one club linked, skip the choosing part
                if (result.list.size == 1) {
                    return@with callback.navigateForward(context, result.list.first())
                }

                bot.send(
                    chatId = context,
                    text = strings.chooseClubMessage,
                    replyMarkup = replyKeyboard(oneTimeKeyboard = true) {
                        result.list.chunked(2).forEach { subList ->
                            row {
                                subList.forEach {
                                    simpleButton(it.name.value)
                                }
                            }
                        }
                        row(simpleReplyButton(strings.goBackChoice))
                    },
                )
                this@AdminChooseClubState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return when (val result = getAllowedClubs.execute()) {
            is GetAllowedClubsUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminChooseClubState, throwable = result.error)
                this@AdminChooseClubState
            }

            is GetAllowedClubsUseCase.Result.Success -> {
                val reply = waitText().first().text

                if (reply == strings.goBackChoice)
                    return@with callback.navigateBack(context)

                val club = result.list.first { it.name.value == reply }
                return@with callback.navigateForward(context, club)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getAllowedClubs: GetAllowedClubsUseCase
    }

    interface Callback {
        fun navigateBack(context: IdChatIdentifier): FSMState<*>
        fun navigateForward(context: IdChatIdentifier, club: com.y9vad9.starix.core.brawlstars.entity.club.Club): FSMState<*>
    }
}
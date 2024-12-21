package com.y9vad9.starix.bot.fsm.admin

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.usecase.GetClubUseCase
import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminChoosePlayersState")
@Serializable
data class AdminChoosePlayersState(
    override val context: IdChatIdentifier,
    val chosenClub: ClubTag,
    val chosenList: List<ClubMember> = listOf(),
    val shouldRenotify: Boolean = false,
    val showToGroup: Boolean,
    val callback: Callback,
) : AdminFSMState<AdminChoosePlayersState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val chosenClub = getClub(strings) { return@with it }

        if (chosenList.isEmpty() || shouldRenotify) {
            // TODO: provide list of chosen players in message
            bot.send(
                chatId = context,
                text = strings.choosePlayersMessage,
                replyMarkup = replyKeyboard {
                    chosenClub.members.chunked(2).forEach {
                        row {
                            it.forEach { player ->
                                simpleButton(player.name.value)
                            }
                        }
                    }
                    row {
                        simpleButton(strings.allPlayersChoice)
                        if (showToGroup)
                            simpleButton(strings.toGroupChoice)
                    }
                    row(simpleReplyButton(strings.continueChoice))
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
        }
        return@with this@AdminChoosePlayersState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val chosenClub = getClub(strings) { return@with it }

        val reply = waitText().first().text
        if (reply == strings.goBackChoice)
            return@with callback.navigateBack(context)

        if (reply == strings.allPlayersChoice)
            return@with callback.navigateForward(
                context = context,
                clubTag = chosenClub.tag,
                toGroup = false,
                selected = chosenClub.members,
            )

        if (reply == strings.toGroupChoice)
            return@with callback.navigateForward(
                context = context,
                selected = emptyList(),
                toGroup = true,
                clubTag = chosenClub.tag
            )

        if (reply == strings.continueChoice) {
            if (chosenList.isEmpty()) {
                bot.send(chatId = context, text = strings.nothingChosenMessage)
                return@with this@AdminChoosePlayersState
            }
            return@with callback.navigateForward(
                context = context,
                selected = chosenList,
                clubTag = chosenClub.tag,
                toGroup = false,
            )
        }
        this@AdminChoosePlayersState
    }

    private suspend inline fun Dependencies.getClub(strings: Strings, or: (FSMState<*>) -> Nothing): Club {
        return when (val result = getClubUseCase.execute(chosenClub)) {
            GetClubUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                or(AdminMainMenuState(context))
            }
            is GetClubUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminChoosePlayersState, result.throwable)
                or(AdminMainMenuState(context))
            }
            is GetClubUseCase.Result.Success -> result.club
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubUseCase: GetClubUseCase
    }

    interface Callback {
        fun navigateBack(context: IdChatIdentifier): FSMState<*>
        fun navigateForward(
            context: IdChatIdentifier,
            selected: List<ClubMember>,
            clubTag: ClubTag,
            toGroup: Boolean,
        ): FSMState<*>
    }
}
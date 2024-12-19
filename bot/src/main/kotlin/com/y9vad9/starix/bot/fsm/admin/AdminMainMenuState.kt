package com.y9vad9.starix.bot.fsm.admin

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminMainMenuState")
@Serializable
data class AdminMainMenuState(
    override val context: IdChatIdentifier,
) : AdminFSMState<FSMState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: FSMState.Dependencies,
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
        this@AdminMainMenuState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: FSMState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return@with when (waitText().first().text) {
            strings.admin.showNonLinkedPlayersChoice -> AdminChooseClubState(
                context = context,
                callback = AdminChooseClubStateNotLinkedPlayersCallback,
            )

            strings.admin.sendMessageChoice -> AdminChooseClubState(
                context = context,
                callback = AdminChooseClubStateSendMessageCallback,
            )

            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminMainMenuState
            }
        }
    }

    @SerialName("AdminMainMenuState.AdminChooseClubStateNotLinkedPlayersCallback")
    @Serializable
    data object AdminChooseClubStateNotLinkedPlayersCallback : AdminChooseClubState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminMainMenuState(context)
        }

        override fun navigateForward(context: IdChatIdentifier, club: com.y9vad9.starix.core.brawlstars.entity.club.Club): FSMState<*> {
            return AdminNotLinkedPlayersState(context, club.tag)
        }
    }

    @SerialName("AdminMainMenuState.AdminChooseClubStateNotLinkedPlayersCallback")
    @Serializable
    data object AdminChooseClubStateSendMessageCallback : AdminChooseClubState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminMainMenuState(context)
        }

        override fun navigateForward(context: IdChatIdentifier, club: com.y9vad9.starix.core.brawlstars.entity.club.Club): FSMState<*> {
            return AdminChoosePlayersState(
                context = context,
                chosenClub = club,
                chosenList = emptyList(),
                shouldRenotify = false,
                showToGroup = true,
                callback = AdminChoosePlayersCallback
            )
        }

        @SerialName("AdminMainMenuState.AdminChooseClubStateNotLinkedPlayersCallback.AdminChoosePlayersStateCallback")
        @Serializable
        data object AdminChoosePlayersCallback : AdminChoosePlayersState.Callback {
            override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
                return AdminMainMenuState(context)
            }

            override fun navigateForward(
                context: IdChatIdentifier,
                selected: List<ClubMember>,
                clubTag: ClubTag,
                toGroup: Boolean,
            ): FSMState<*> {
                return AdminSendMessageState(
                    context = context,
                    chosenList = selected,
                    clubTag = clubTag,
                    sendToGroup = toGroup,
                )
            }
        }
    }
}
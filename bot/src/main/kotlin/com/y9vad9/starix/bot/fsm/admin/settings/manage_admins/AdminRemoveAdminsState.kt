package com.y9vad9.starix.bot.fsm.admin.settings.manage_admins

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.entity.player.Player
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.admin.GetAdminsListUseCase
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.admin.RemoveAdminUseCase
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

@SerialName("AdminRemoveAdminsState")
@Serializable
data class AdminRemoveAdminsState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminRemoveAdminsState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        getAdminsList { players ->
            bot.send(
                chatId = context,
                entities = strings.admin.settings.adminListWithChooseMessage(players),
                replyMarkup = replyKeyboard {
                    players.chunked(2).forEach { subList ->
                        row {
                            subList.forEach {
                                simpleButton(it.name.value)
                            }
                        }
                    }
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
            this@AdminRemoveAdminsState
        }
        this@AdminRemoveAdminsState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val text = waitText().first().text

        if (text == strings.goBackChoice) {
            return@with AdminViewAdminsState(context, clubTag)
        }

        getAdminsList { players ->
            val player = players.firstOrNull {
                it.name.value == text
            } ?: return@getAdminsList run {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminRemoveAdminsState
            }

            when (val result = removeAdmin.execute(clubTag, player.tag)) {
                is RemoveAdminUseCase.Result.Failure -> {
                    logAndProvideMessage(this@AdminRemoveAdminsState, result.error)
                    this@AdminRemoveAdminsState
                }

                RemoveAdminUseCase.Result.Success -> {
                    bot.send(
                        chatId = context,
                        entities = strings.admin.settings.adminWasRemovedMessage(player),
                    )
                    this@AdminRemoveAdminsState
                }

                RemoveAdminUseCase.Result.UserNotLinkedAccount -> {
                    bot.send(
                        chatId = context,
                        text = strings.accountIsNotLinked,
                    )
                    this@AdminRemoveAdminsState
                }
            }
        }
    }

    private suspend fun Dependencies.getAdminsList(
        onSuccess: suspend (List<com.y9vad9.starix.core.brawlstars.entity.player.Player>) -> FSMState<*>,
    ): FSMState<*> {
        return when (val result = getAdminsList.execute(clubTag)) {
            is GetAdminsListUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminRemoveAdminsState, result.error)
                AdminMainMenuState(context)
            }

            is GetAdminsListUseCase.Result.Success -> {
                onSuccess(result.players)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getAdminsList: GetAdminsListUseCase
        val removeAdmin: RemoveAdminUseCase
    }
}
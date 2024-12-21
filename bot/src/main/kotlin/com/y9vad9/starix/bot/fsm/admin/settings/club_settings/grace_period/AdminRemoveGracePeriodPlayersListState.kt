package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.grace_period

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.bot.ext.asTelegramUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.usecase.excused.GetListOfExcusedPlayersUseCase
import com.y9vad9.starix.core.brawlstars.usecase.excused.UnexcusePlayerUseCase
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

@SerialName("AdminRemoveGracePeriodPlayersState")
@Serializable
data class AdminRemoveGracePeriodPlayersState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminRemoveGracePeriodPlayersState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        getPlayersInClubWithGracePeriod(dependencies, strings) { members ->
            bot.send(
                chatId = context,
                entities = strings.admin.settings.gracePeriodMessage(members),
                replyMarkup = replyKeyboard {
                    members.keys.chunked(2).forEach { sublist ->
                        row {
                            sublist.forEach {
                                simpleButton(it.name.value)
                            }
                        }
                    }
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
            this@AdminRemoveGracePeriodPlayersState
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val input = waitText().first().text) {
            strings.goBackChoice -> AdminGracePeriodPlayersListState(context, clubTag)
            else -> {
                getPlayersInClubWithGracePeriod(dependencies, strings) { excused ->
                    val player = excused.keys.firstOrNull { it.name.value == input }
                        ?: run {
                            bot.send(
                                chatId = context,
                                text = strings.invalidChoiceMessage,
                            )
                            return@getPlayersInClubWithGracePeriod this@AdminRemoveGracePeriodPlayersState
                        }

                    when (val result = unexcusePlayer.execute(context.asTelegramUserId(), player.tag, clubTag)) {
                        is UnexcusePlayerUseCase.Result.Failure -> {
                            logAndProvideMessage(
                                state = this@AdminRemoveGracePeriodPlayersState,
                                throwable = result.error,
                            )
                            AdminViewClubSettingsState(context, clubTag)
                        }

                        UnexcusePlayerUseCase.Result.NoPermission -> {
                            bot.send(
                                chatId = context,
                                text = strings.noPermissionMessage,
                            )
                            CommonInitialState(context)
                        }

                        UnexcusePlayerUseCase.Result.Success -> {
                            bot.send(
                                chatId = context,
                                text = strings.admin.settings.successfullyChangedOption,
                            )
                            AdminGracePeriodPlayersListState(context, clubTag)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getPlayersInClubWithGracePeriod(
        dependencies: Dependencies,
        strings: Strings,
        onSuccess: suspend (Map<ClubMember, UnixTime>) -> FSMState<*>,
    ): FSMState<*> = with(dependencies) {
        return when (val result = getListOfExcusedPlayers.execute(context.asTelegramUserId(), clubTag)) {
            is GetListOfExcusedPlayersUseCase.Result.Success -> {
                onSuccess(result.players)
            }

            is GetListOfExcusedPlayersUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminRemoveGracePeriodPlayersState, result.error)
                AdminViewClubSettingsState(context, clubTag)
            }

            GetListOfExcusedPlayersUseCase.Result.NoPermission -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getListOfExcusedPlayers: GetListOfExcusedPlayersUseCase
        val unexcusePlayer: UnexcusePlayerUseCase
    }
}
package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.grace_period

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.brawlstars.usecase.excused.GetListOfExcusedPlayersUseCase
import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminGracePeriodPlayersListState")
@Serializable
data class AdminGracePeriodPlayersListState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminGracePeriodPlayersListState.Dependencies> {
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
                    row(simpleReplyButton(strings.addChoice))
                    row(simpleReplyButton(strings.removeChoice))
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
            this@AdminGracePeriodPlayersListState
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (waitText().first().text) {
            strings.goBackChoice -> AdminViewClubSettingsState(context, clubTag)
            strings.addChoice -> AdminAddGracePeriodPlayerState()
            strings.removeChoice -> TODO()
            else -> {
                bot.send(chatId = context, text = strings.invalidChoiceMessage)
                this@AdminGracePeriodPlayersListState
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
                logAndProvideMessage(this@AdminGracePeriodPlayersListState, result.error)
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
    }
}
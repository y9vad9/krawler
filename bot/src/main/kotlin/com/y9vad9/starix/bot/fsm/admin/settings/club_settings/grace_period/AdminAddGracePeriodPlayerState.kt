package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.grace_period

import com.y9vad9.starix.bot.ext.asTelegramUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminChoosePlayersState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.bot.fsm.components.calendar.DatePickerComponentState
import com.y9vad9.starix.bot.fsm.components.calendar.YearPickerComponentState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.usecase.excused.ExcusePlayerUseCase
import com.y9vad9.starix.core.brawlstars.usecase.excused.GetListOfExcusedPlayersUseCase
import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
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

@SerialName("AdminAddGracePeriodPlayerState")
@Serializable
data class AdminAddGracePeriodPlayerState(
    override val context: IdChatIdentifier,
    val club: Club,
    val chosenPlayers: List<ClubMember>,
    val untilTime: Long? = null,
) : FSMState<AdminAddGracePeriodPlayerState.Dependencies> {
    private val clubTag get() = club.tag

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        if (chosenPlayers.isEmpty()) {
            return@with AdminChoosePlayersState(
                context = context,
                chosenClub = club,
            )
        }

        if (untilTime != null) {
            @OptIn(ValidationDelicateApi::class)
            val result = excusePlayer.execute(
                id = context.asTelegramUserId(),
                playerTag = chosenPlayers.map { it.tag },
                clubTag = clubTag,
                untilTime = UnixTime.createUnsafe(untilTime),
            )

            return@with when (result) {
                is ExcusePlayerUseCase.Result.Failure -> {
                    logAndProvideMessage(this@AdminAddGracePeriodPlayerState, result.error)
                    AdminGracePeriodPlayersListState(context, clubTag)
                }

                ExcusePlayerUseCase.Result.NoPermission -> {
                    bot.send(
                        chatId = context,
                        text = strings.noPermissionMessage,
                    )
                    CommonInitialState(context)
                }

                ExcusePlayerUseCase.Result.Success -> {
                    bot.send(
                        chatId = context,
                        text = strings.admin.settings.gracePeriodSuccessMessage,
                    )
                    AdminGracePeriodPlayersListState(context, clubTag)
                }
            }
        }
        getPlayersInClubWithGracePeriod(dependencies, strings) { members ->
            bot.send(
                chatId = context,
                entities = strings.admin.settings.gracePeriodMessage(members),
                replyMarkup = replyKeyboard {
                    members.keys.chunked(2).forEach {
                        row {
                            it.forEach { player ->
                                simpleButton(player.name.value)
                            }
                        }
                    }
                    row(simpleReplyButton(strings.continueChoice))
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
            this@AdminAddGracePeriodPlayerState
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val input = waitText().first().text) {
            strings.goBackChoice -> AdminGracePeriodPlayersListState(context, clubTag)
            strings.continueChoice -> {
                YearPickerComponentState(
                    context = context,
                    callback = DatePickerComponentStateCallback(club, chosenPlayers),
                    canPickPast = false,
                    shouldPickTime = true,
                )
            }

            else -> {
                getPlayersInClubWithGracePeriod(dependencies, strings) { excused ->
                    val player = excused.keys.firstOrNull { it.name.value == input }
                        ?: run {
                            bot.send(
                                chatId = context,
                                text = strings.invalidChoiceMessage,
                            )
                            return@getPlayersInClubWithGracePeriod this@AdminAddGracePeriodPlayerState
                        }
                    this@AdminAddGracePeriodPlayerState.copy(chosenPlayers = chosenPlayers + player)
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
                logAndProvideMessage(this@AdminAddGracePeriodPlayerState, result.error)
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

    @SerialName("AdminAddGracePeriodPlayerState.DatePickerComponentStateCallback")
    @Serializable
    private data class DatePickerComponentStateCallback(
        val selectedClub: Club,
        val selectedPlayers: List<ClubMember>,
    ) : DatePickerComponentState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminChoosePlayersState(
                context = context,
                chosenList = selectedPlayers,
                chosenClub = selectedClub,
                shouldRenotify = true,
                showToGroup = false,
                callback = AdminChoosePlayersStateCallback(selectedClub),
            )
        }

        override fun navigateForward(
            context: IdChatIdentifier,
            dateTime: UnixTime,
        ): FSMState<*> {
            return AdminGracePeriodPlayersListState(context, selectedClub.tag)
        }

        @SerialName("AdminAddGracePeriodPlayerState.DatePickerComponentStateCallback.AdminChoosePlayersStateCallback")
        @Serializable
        private data class AdminChoosePlayersStateCallback(
            val club: Club,
        ) : AdminChoosePlayersState.Callback {
            override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
                return AdminAddGracePeriodPlayerState(context, club, emptyList(), null)
            }

            override fun navigateForward(
                context: IdChatIdentifier,
                selected: List<ClubMember>,
                clubTag: ClubTag,
                toGroup: Boolean,
            ): FSMState<*> {
                return YearPickerComponentState(
                    context = context,
                    canPickPast = false,
                    shouldPickTime = true,
                    callback = DatePickerComponentStateCallback(club, selected)
                )
            }

            @SerialName("AdminAddGracePeriodPlayerState.DatePickerComponentStateCallback.AdminChoosePlayersStateCallback.DatePickerComponentStateCallback")
            @Serializable
            private data class DatePickerComponentStateCallback(
                val club: Club,
                val selected: List<ClubMember>,
            ) : DatePickerComponentState.Callback {
                override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
                    return AdminChoosePlayersState(
                        context = context,
                        chosenClub = club,
                        chosenList = emptyList(),
                        shouldRenotify = false,
                        showToGroup = false,
                        callback = AdminChoosePlayersStateCallback(club),
                    )
                }

                override fun navigateForward(context: IdChatIdentifier, dateTime: UnixTime): FSMState<*> {
                    return AdminAddGracePeriodPlayerState(
                        context = context,
                        club = club,
                        chosenPlayers = selected,
                        untilTime = dateTime.inMilliseconds,
                    )
                }
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getListOfExcusedPlayers: GetListOfExcusedPlayersUseCase
        val excusePlayer: ExcusePlayerUseCase
    }
}
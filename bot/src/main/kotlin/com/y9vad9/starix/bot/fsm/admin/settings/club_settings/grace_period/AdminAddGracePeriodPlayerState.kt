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
import dev.inmo.tgbotapi.types.IdChatIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminAddGracePeriodPlayerState")
@Serializable
data class AdminAddGracePeriodPlayerState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val chosenPlayers: List<ClubMember>,
    val untilTime: Long? = null,
) : FSMState<AdminAddGracePeriodPlayerState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        if (chosenPlayers.isEmpty()) {
            return@with AdminChoosePlayersState(
                context = context,
                chosenClub = clubTag,
                callback = AdminChoosePlayersStateCallback(clubTag),
                shouldRenotify = false,
                showToGroup = false,
            )
        }
        return@with this@AdminAddGracePeriodPlayerState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

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
        } else {
            return@with YearPickerComponentState(
                context = context,
                callback = AdminChoosePlayersStateCallback.DatePickerComponentStateCallback(clubTag, chosenPlayers),
                canPickPast = false,
                shouldPickTime = true,
            )
        }
    }

    @SerialName("AdminAddGracePeriodPlayerState.DatePickerComponentStateCallback.AdminChoosePlayersStateCallback")
    @Serializable
    private data class AdminChoosePlayersStateCallback(
        val club: ClubTag,
    ) : AdminChoosePlayersState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminGracePeriodPlayersListState(context, club)
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

        @SerialName("AdminAddGracePeriodPlayerState.DatePickerComponentStateCallback")
        @Serializable
        data class DatePickerComponentStateCallback(
            val selectedClub: ClubTag,
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
                return AdminGracePeriodPlayersListState(context, selectedClub)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getListOfExcusedPlayers: GetListOfExcusedPlayersUseCase
        val excusePlayer: ExcusePlayerUseCase
    }
}
package com.y9vad9.starix.bot.fsm.admin.settings.club_settings

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminChooseClubState
import com.y9vad9.starix.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.chat_rules.AdminViewChatRulesSettingState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.club_rules.AdminViewClubRulesSettingState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.grace_period.AdminGracePeriodPlayersListState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.join_request.AdminManageJoinRequestSettingState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.player_linkage.AdminPlayersLinkageSettingState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.trophies_requirement.AdminViewTrophiesRequirementSettingState
import com.y9vad9.starix.bot.fsm.admin.settings.manage_admins.AdminViewAdminsState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.core.brawlstars.entity.club.Club
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.usecase.settings.admin.club.GetClubSettingsUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminViewClubSettingsState")
@Serializable
data class AdminViewClubSettingsState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminViewClubSettingsState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val result = getClubSettings.execute(clubTag)) {
            GetClubSettingsUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminChooseClubState(
                    context = context,
                    callback = AdminChooseClubStateCallback,
                )
            }

            is GetClubSettingsUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    entities = strings.admin.settings.clubSettingsMessage(result.clubSettings),
                    replyMarkup = replyKeyboard {
                        sequenceOf(
                            strings.admin.settings.multiplePlayersChoice,
                            strings.admin.settings.joinRequestsChoice,
                            strings.admin.settings.clubRulesChoice,
                            strings.admin.settings.chatRulesChoice,
                            strings.admin.settings.gracePeriodChoice,
                            strings.admin.settings.trophieRequirementsChoice,
                            strings.admin.settings.playersLinkageChoice,
                            strings.admin.settings.linkedGroupChoice,
                            strings.admin.settings.adminsListChoice,
                        ).map { SimpleKeyboardButton(it) }.chunked(2).forEach {
                            add(it)
                        }

                        row(simpleReplyButton(strings.goBackChoice))
                    }
                )
                this@AdminViewClubSettingsState
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (waitText().first().text) {
            strings.admin.settings.multiplePlayersChoice ->
                AdminManageMultiplayerSettingState(context, clubTag)

            strings.admin.settings.joinRequestsChoice ->
                AdminManageJoinRequestSettingState(context, clubTag)

            strings.admin.settings.clubRulesChoice ->
                AdminViewClubRulesSettingState(context, clubTag)

            strings.admin.settings.chatRulesChoice ->
                AdminViewChatRulesSettingState(context, clubTag)

            strings.admin.settings.gracePeriodChoice -> AdminGracePeriodPlayersListState(context, clubTag)
            strings.admin.settings.trophieRequirementsChoice -> AdminViewTrophiesRequirementSettingState(context, clubTag)

            strings.admin.settings.playersLinkageChoice ->
                AdminPlayersLinkageSettingState(context, clubTag)

            strings.admin.settings.linkedGroupChoice -> TODO()
            strings.admin.settings.adminsListChoice ->
                AdminViewAdminsState(context, clubTag)

            strings.goBackChoice -> AdminMainMenuState(context)
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminViewClubSettingsState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getClubSettings: GetClubSettingsUseCase
    }

    @SerialName("AdminViewClubSettingsState.AdminChooseClubStateCallback")
    @Serializable
    data object AdminChooseClubStateCallback : AdminChooseClubState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return AdminMainMenuState(context)
        }

        override fun navigateForward(context: IdChatIdentifier, club: com.y9vad9.starix.core.brawlstars.entity.club.Club): FSMState<*> {
            return AdminViewClubSettingsState(context, club.tag)
        }
    }
}
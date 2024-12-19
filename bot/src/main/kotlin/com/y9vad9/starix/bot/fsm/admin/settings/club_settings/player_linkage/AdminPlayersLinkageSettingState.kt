package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.player_linkage

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.starix.bot.fsm.admin.settings.club_settings.AdminViewClubSettingsState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.brawlstars.usecase.GetPlayersInClubWithLinkageUseCase
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

@SerialName("AdminPlayersLinkageSettingState")
@Serializable
data class AdminPlayersLinkageSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminPlayersLinkageSettingState.Dependencies> {
    companion object {
        private const val ACCOUNT_LINKED_SYMBOL = "\uD83D\uDFE2"
        private const val ACCOUNT_NOT_LINKED_SYMBOL = "\uD83D\uDD34"
    }

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        getPlayersInClub(dependencies, strings) { members ->
            bot.send(
                chatId = context,
                entities = strings.admin.settings.viewPlayersLinkageMessage,
                replyMarkup = replyKeyboard {
                    members.map {
                        simpleReplyButton(
                            buildString {
                                append(if (it.value) ACCOUNT_LINKED_SYMBOL else ACCOUNT_NOT_LINKED_SYMBOL)
                                append(" ${it.key.name.value}")
                            }
                        )
                    }.chunked(2).forEach { subList ->
                        row {
                            subList.forEach {
                                add(it)
                            }
                        }
                    }
                },
            )
            this@AdminPlayersLinkageSettingState
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val name = waitText()
            .first()
            .text
            .replace(Regex("[$ACCOUNT_LINKED_SYMBOL$ACCOUNT_NOT_LINKED_SYMBOL] "), "")

        if (name == strings.goBackChoice)
            return@with AdminViewClubSettingsState(context, clubTag)

        getPlayersInClub(dependencies, strings) { map ->
            val member = map.keys.firstOrNull { it.name.value == name }
                ?: run {
                    bot.send(
                        chatId = context,
                        text = strings.invalidChoiceMessage,
                    )
                    return@getPlayersInClub this@AdminPlayersLinkageSettingState
                }

            TODO()
        }
    }

    private suspend fun getPlayersInClub(
        dependencies: Dependencies,
        strings: Strings,
        onSuccess: suspend (Map<ClubMember, Boolean>) -> FSMState<*>,
    ): FSMState<*> = with(dependencies) {
        return when (val result = getPlayersInClub.execute(clubTag)) {
            GetPlayersInClubWithLinkageUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }

            is GetPlayersInClubWithLinkageUseCase.Result.Success -> {
                onSuccess(result.members)
            }

            is GetPlayersInClubWithLinkageUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminPlayersLinkageSettingState, result.throwable)
                AdminViewClubSettingsState(context, clubTag)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getPlayersInClub: GetPlayersInClubWithLinkageUseCase
    }
}
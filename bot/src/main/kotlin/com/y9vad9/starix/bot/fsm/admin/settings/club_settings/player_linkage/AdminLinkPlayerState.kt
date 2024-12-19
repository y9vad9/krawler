package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.player_linkage

import com.y9vad9.starix.bot.ext.asTelegramUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.common.CommonInitialState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.usecase.LinkPlayerUseCase
import com.y9vad9.starix.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.foundation.validation.createOrNull
import com.y9vad9.starix.localization.Strings
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminLinkPlayerState")
@Serializable
data class AdminLinkPlayerState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val member: ClubMember,
) : FSMState<AdminLinkPlayerState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.admin.settings.linkPlayerMessage,
            replyMarkup = replyKeyboard {
                row {
                    simpleButton(strings.goBackChoice)
                }
            },
        )
        this@AdminLinkPlayerState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val reply = waitText().first().text) {
            strings.goBackChoice -> AdminPlayersLinkageSettingState(context, clubTag)
            else -> {
                val id = reply.toLongOrNull()
                    ?.let {
                        TelegramUserId.createOrNull(it)
                    }
                    ?: return@with run {
                        bot.send(
                            chatId = context,
                            text = strings.constructor.telegramUserIdFailure,
                        )
                        this@AdminLinkPlayerState
                    }

                linkPlayer(dependencies, id, strings)
            }
        }
    }

    private suspend fun linkPlayer(
        dependencies: Dependencies,
        userId: TelegramUserId,
        strings: Strings,
    ): FSMState<*> = with(dependencies) {
        when (val result = linkPlayerUseCase.execute(context.asTelegramUserId(), member.tag, userId)) {
            is LinkPlayerUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminLinkPlayerState, result.error)
                AdminPlayersLinkageSettingState(context, clubTag)
            }

            LinkPlayerUseCase.Result.NoPermission -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }

            LinkPlayerUseCase.Result.PlayerNotInTheClub -> {
                bot.send(
                    chatId = context,
                    text = strings.playerNotInTheClubMessage,
                )
                AdminPlayersLinkageSettingState(context, clubTag)
            }

            LinkPlayerUseCase.Result.Success -> AdminManagePlayerLinkageSettingState(
                context, clubTag, member, true,
            )

            LinkPlayerUseCase.Result.ShouldUnlinkFirst -> {
                bot.send(
                    chatId = context,
                    text = strings.admin.settings.shouldUnlinkFirstMessage,
                )
                AdminManagePlayerLinkageSettingState(context, clubTag, member, true)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val linkPlayerUseCase: LinkPlayerUseCase
    }
}
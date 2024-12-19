package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.starix.bot.ext.asTelegramUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.common.CommonPromptPlayerTagState.Purpose
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.bot.fsm.member.MemberMainMenuState
import com.y9vad9.starix.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.starix.core.system.entity.ClubJoinAbility
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.telegram.usecase.AddMemberToChatUseCase
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("CommonWantJoinChatState")
@Serializable
class CommonWantJoinChatState(
    override val context: IdChatIdentifier,
    val promptedTag: PlayerTag,
) : CommonFSMState<CommonWantJoinChatState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return@with when (val result = addMemberToChat.execute(promptedTag, context.asTelegramUserId())) {
            is AddMemberToChatUseCase.Result.Success -> {
                if (result.shouldProvideChatLink) {
                    bot.send(
                        chatId = context,
                        entities = strings.wantToJoinClubSuccessMessage(result.player, result.club),
                        linkPreviewOptions = LinkPreviewOptions.Disabled,
                    )
                    CommonClubRulesState(context, result.club.tag!!, result.link!!)
                } else {
                    bot.send(
                        chatId = context,
                        text = strings.youAreAlreadyInTheChat,
                        replyMarkup = ReplyKeyboardRemove()
                    )
                    // todo savedChoices
                    MemberMainMenuState(context)
                }
            }

            is AddMemberToChatUseCase.Result.Failure -> {
                logAndProvideMessage(this@CommonWantJoinChatState, result.throwable)
                // TODO: savedChoices
                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = Purpose.JOIN_CHAT,
                )
            }

            is AddMemberToChatUseCase.Result.NotInTheClub -> {
                bot.send(
                    chatId = context,
                    entities = strings.notInTheClubMessage(result.clubs),
                    replyMarkup = replyKeyboard {
                        if (result.clubs.any { ability -> ability is ClubJoinAbility.UponRequest })
                            add(listOf(SimpleKeyboardButton(strings.applyForClubChoice)))
                        add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                    }
                )
                this@CommonWantJoinChatState
            }

            AddMemberToChatUseCase.Result.PlayerNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.playerTagNotFoundMessage,
                    replyMarkup = ReplyKeyboardRemove()
                )

                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = Purpose.JOIN_CHAT,
                )
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val reply = waitText().first().text
        return@with when (reply) {
            // todo savedChoices
            strings.goBackChoice -> CommonPromptPlayerTagState(context, emptyList(), Purpose.JOIN_CHAT)
            strings.applyForClubChoice -> TODO()
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@CommonWantJoinChatState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val addMemberToChat: AddMemberToChatUseCase
        val settingsRepository: SettingsRepository
    }
}
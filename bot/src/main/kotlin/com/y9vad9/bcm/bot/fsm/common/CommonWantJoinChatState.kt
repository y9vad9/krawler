package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState.Purpose
import com.y9vad9.bcm.bot.fsm.createLoggingMessage
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.system.repository.SettingsRepository
import com.y9vad9.bcm.core.telegram.usecase.AddMemberToChatUseCase
import com.y9vad9.bcm.core.user.entity.ClubJoinAbility
import com.y9vad9.bcm.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.bcm.foundation.validation.createUnsafe
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
class CommonWantJoinChatState private constructor(
    override val context: IdChatIdentifier,
    val promptedTag: String,
) : CommonFSMState<CommonWantJoinChatState, CommonWantJoinChatState.Dependencies> {
    constructor(context: IdChatIdentifier, promptedTag: PlayerTag) : this(context, promptedTag.toString())

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *>,
        dependencies: Dependencies,
    ): FSMState<*, *>? = with(dependencies) {
        @OptIn(ValidationDelicateApi::class)
        val tag = PlayerTag.createUnsafe(promptedTag)

        return@with when (val result = addMemberToChat.execute(tag, context.asTelegramUserId())) {
            is AddMemberToChatUseCase.Result.AlreadyIn -> {
                if (result.shouldProvideChatLink) {
                    bot.send(
                        chatId = context,
                        text = strings.youAreRegisteredButNotInChatMessage(result.player),
                    )
                    CommonChatRulesState(context, result.clubTag!!, result.link!!)
                } else {
                    bot.send(
                        chatId = context,
                        text = strings.playerAlreadyLinkedBySomeoneMessage,
                        replyMarkup = ReplyKeyboardRemove()
                    )
                    // todo savedChoices
                    CommonPromptPlayerTagState(context, emptyList(), Purpose.JOIN_CHAT)
                }
            }

            is AddMemberToChatUseCase.Result.Failure -> {
                createLoggingMessage(logger, result.throwable)
                bot.send(
                    chatId = context,
                    text = strings.failureToMessage(result.throwable),
                )
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
                    text = strings.notInTheClubMessage(result.clubs),
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
                    text = strings.playerNotFoundMessage,
                    replyMarkup = ReplyKeyboardRemove()
                )

                CommonPromptPlayerTagState(
                    context = context,
                    savedChoices = emptyList(),
                    purpose = Purpose.JOIN_CHAT,
                )
            }

            is AddMemberToChatUseCase.Result.Success -> {
                bot.send(
                    chatId = context,
                    text = strings.commonWantJoinChatStateSuccessMessage,
                    replyMarkup = ReplyKeyboardRemove(),
                )
                CommonChatRulesState(context, result.clubTag, result.inviteLink)
            }
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*, *>>.process(
        dependencies: Dependencies,
        state: CommonWantJoinChatState,
    ): FSMState<*, *>? = with(dependencies) {
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
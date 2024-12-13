package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.telegram.usecase.LinkBrawlStarsPlayerUseCase
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
data class CommonPromptPlayerTagState(
    override val context: IdChatIdentifier,
    val savedChoices: List<String>,
    val purpose: Purpose
) : CommonFSMState<CommonPromptPlayerTagState, CommonPromptPlayerTagState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *>,
        dependencies: Dependencies,
    ): FSMState<*, *>? = with(dependencies) {
        bot.send(
            chatId = context,
            text = strings.letsLinkBsMessage,
            replyMarkup = if (savedChoices.isEmpty()) ReplyKeyboardRemove() else replyKeyboard {
                savedChoices.chunked(2).forEach {
                    add(buildList {
                        add(SimpleKeyboardButton(it.first()))

                        if(it.size == 2)
                            add(SimpleKeyboardButton(it.last()))
                    })

                    add(listOf(SimpleKeyboardButton(strings.goBackChoice)))
                }
            }
        )
        this@CommonPromptPlayerTagState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*, *>>.process(
        dependencies: Dependencies,
        state: CommonPromptPlayerTagState,
    ): FSMState<*, *>? = with(dependencies) {
        val input = waitText().first().text

        if (input == strings.goBackChoice)
            return@with getStateToGoBack()

        val tag = PlayerTag.create(waitText().first().text)
            .getOrElse { exception ->
                bot.send(context, strings.invalidTagFormatOrSizeMessage)
                return@with this@CommonPromptPlayerTagState
            }

        return when (val result = linkBrawlStarsPlayer.execute(context.asTelegramUserId(), tag)) {
            LinkBrawlStarsPlayerUseCase.Result.AlreadyLinked -> {
                bot.send(context, strings.playerAlreadyLinkedBySomeoneMessage)
                return@with this@CommonPromptPlayerTagState
            }
            is LinkBrawlStarsPlayerUseCase.Result.Failure -> {
                bot.send(context, strings.failureToMessage(result.throwable))
                return@with this@CommonPromptPlayerTagState
            }
            LinkBrawlStarsPlayerUseCase.Result.PlayerDoesNotExists -> {
                bot.send(context, strings.playerNotFoundMessage)
                return@with this@CommonPromptPlayerTagState
            }
            is LinkBrawlStarsPlayerUseCase.Result.Success -> {
                bot.send(context, strings.successfullyLinkedBsMessage(result.player))
                getStateToGoForward()
            }
        }
    }

    private fun getStateToGoForward(): FSMState<*, *> {
        return when (purpose) {
            Purpose.EXPLORE_CLUBS -> TODO()
            Purpose.JOIN_CHAT -> TODO()
            Purpose.GUEST_LINK -> TODO()
            Purpose.MEMBER_LINK -> TODO()
        }
    }

    private fun getStateToGoBack(): FSMState<*, *> {
        return when (purpose) {
            Purpose.EXPLORE_CLUBS -> GuestMainMenuState(context)
            Purpose.JOIN_CHAT -> TODO()
            Purpose.GUEST_LINK -> TODO()
            Purpose.MEMBER_LINK -> TODO()
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val linkBrawlStarsPlayer: LinkBrawlStarsPlayerUseCase
    }

    @Serializable
    enum class Purpose {
        EXPLORE_CLUBS, JOIN_CHAT, GUEST_LINK, MEMBER_LINK
    }
}
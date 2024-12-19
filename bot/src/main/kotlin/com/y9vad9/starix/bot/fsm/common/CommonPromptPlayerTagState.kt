package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.guest.GuestExploreClubsState
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.player.value.PlayerTag
import com.y9vad9.bcm.core.telegram.usecase.LinkBrawlStarsPlayerUseCase
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

@SerialName("CommonPromptPlayerTagState")
@Serializable
data class CommonPromptPlayerTagState(
    override val context: IdChatIdentifier,
    val savedChoices: List<String>,
    val purpose: Purpose,
) : CommonFSMState<CommonPromptPlayerTagState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.letsLinkBsMessage,
            replyMarkup = replyKeyboard {
                savedChoices.chunked(2).forEach { subList ->
                    row {
                        subList.forEach {
                            simpleButton(it)
                        }
                    }
                }
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@CommonPromptPlayerTagState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val input = waitText().first().text

        if (input == strings.goBackChoice) {
            return@with getStateToGoBack()
        }

        val tag = PlayerTag.create(input)
            .getOrElse { _ ->
                bot.send(context, strings.invalidTagFormatOrSizeMessage)
                return@with this@CommonPromptPlayerTagState
            }

        return when (val result = linkBrawlStarsPlayer.execute(context.asTelegramUserId(), tag)) {
            LinkBrawlStarsPlayerUseCase.Result.AlreadyLinked -> {
                bot.send(context, strings.playerAlreadyLinkedBySomeoneMessage)
                return@with this@CommonPromptPlayerTagState
            }

            is LinkBrawlStarsPlayerUseCase.Result.Failure -> {
                logAndProvideMessage(this@CommonPromptPlayerTagState, result.throwable)
                return@with this@CommonPromptPlayerTagState
            }

            LinkBrawlStarsPlayerUseCase.Result.PlayerDoesNotExists -> {
                bot.send(context, strings.playerTagNotFoundMessage)
                return@with this@CommonPromptPlayerTagState
            }

            is LinkBrawlStarsPlayerUseCase.Result.Success -> {
                bot.send(context, strings.successfullyLinkedBsMessage(result.player))
                getStateToGoForward(tag)
            }
        }
    }

    private fun getStateToGoForward(tag: PlayerTag): FSMState<*> {
        return when (purpose) {
            Purpose.EXPLORE_CLUBS -> GuestExploreClubsState(context, tag)
            Purpose.JOIN_CHAT -> CommonWantJoinChatState(context, tag)
            Purpose.GUEST_LINK -> TODO()
            Purpose.MEMBER_LINK -> TODO()
        }
    }

    private fun getStateToGoBack(): FSMState<*> {
        return when (purpose) {
            Purpose.EXPLORE_CLUBS, Purpose.JOIN_CHAT -> GuestMainMenuState(context)
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
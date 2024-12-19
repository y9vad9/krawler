package com.y9vad9.starix.bot.fsm.admin

import com.y9vad9.starix.bot.asChatId
import com.y9vad9.starix.bot.asUserId
import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.logAndProvideMessage
import com.y9vad9.starix.core.brawlstars.entity.club.ClubMember
import com.y9vad9.starix.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.starix.core.system.repository.SettingsRepository
import com.y9vad9.starix.core.system.usecase.GetUsersByTagUseCase
import dev.inmo.tgbotapi.extensions.api.send.copy
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitTextedContentMessage
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.caption
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextedContent
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("AdminSendMessageState")
@Serializable
class AdminSendMessageState(
    override val context: IdChatIdentifier,
    val chosenList: List<ClubMember> = listOf(),
    val clubTag: ClubTag,
    val sendToGroup: Boolean = false,
) : AdminFSMState<AdminSendMessageState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.admin.provideMessageForPlayers,
            replyMarkup = replyKeyboard {
                row(simpleReplyButton(strings.goBackChoice))
            }
        )

        this@AdminSendMessageState
    }

    @OptIn(RiskFeature::class)
    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val message: CommonMessage<TextedContent> = waitTextedContentMessage().first()

        if (message.text == strings.goBackChoice) {
            return@with AdminMainMenuState(context)
        }

        if (message.text.isNullOrBlank() && message.caption.isNullOrBlank()) {
            bot.send(
                chatId = context,
                text = strings.noMessageError,
            )
            return@with this@AdminSendMessageState
        }

        when (val result = getUsersByTag.execute(chosenList.map { it.tag })) {
            is GetUsersByTagUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminSendMessageState, result.throwable)
            }

            is GetUsersByTagUseCase.Result.Success -> {
                if (!sendToGroup) {
                    globalScope.launch {
                        result.users.forEach { user ->
                            try {
                                bot.copy(
                                    toChatId = user.telegramAccount!!.id.asUserId(),
                                    fromChatId = context,
                                    messageIds = listOf(message.messageId),
                                )
                            } catch (e: Exception) {
                                logAndProvideMessage(this@AdminSendMessageState, e)
                            }
                            delay(3000L) // to avoid restrictions
                        }
                    }
                } else {
                    val linkedGroup = settingsRepository.getSettings()
                        .allowedClubs[clubTag]!!
                        .linkedTelegramChat?.asChatId() ?: run {
                        bot.send(
                            chatId = context,
                            entities = strings.clubIsNotLinkedToGroup,
                        )
                        return@with AdminMainMenuState(context)
                    }
                    bot.copy(
                        toChatId = linkedGroup,
                        fromChatId = context,
                        messageIds = listOf(message.messageId),
                    )
                }
                bot.send(chatId = context, text = strings.admin.sendMessageSuccessMessage)
            }
        }
        AdminMainMenuState(context)
    }

    interface Dependencies : FSMState.Dependencies {
        val getUsersByTag: GetUsersByTagUseCase
        val settingsRepository: SettingsRepository
    }
}
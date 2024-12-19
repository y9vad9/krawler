@file:Suppress("UNCHECKED_CAST")
@file:OptIn(ValidationDelicateApi::class)

package com.y9vad9.starix.bot

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.ext.launchRestartable
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.*
import com.y9vad9.bcm.bot.fsm.common.*
import com.y9vad9.bcm.bot.fsm.guest.GuestExploreClubsState
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.bcm.bot.fsm.member.MemberMainMenuState
import com.y9vad9.bcm.bot.handler.handleBotPm
import com.y9vad9.bcm.core.system.usecase.CheckIsUserAllowedToJoinUseCase
import com.y9vad9.bcm.core.system.usecase.GetLinkedPlayerInChatUseCase
import com.y9vad9.bcm.core.telegram.entity.value.TelegramGroupId
import com.y9vad9.bcm.core.telegram.entity.value.TelegramUserId
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
import dev.inmo.tgbotapi.extensions.api.chat.invite_links.approve
import dev.inmo.tgbotapi.extensions.api.chat.members.promoteChatAdministrator
import dev.inmo.tgbotapi.extensions.api.chat.members.setChatAdministratorCustomTitle
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithFSM
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPolling
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.LinkPreviewOptions
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.chat.member.AdministratorChatMember
import dev.inmo.tgbotapi.types.chat.member.LeftChatMember
import dev.inmo.tgbotapi.types.chat.member.MemberChatMember
import dev.inmo.tgbotapi.updateshandlers.FlowsUpdatesFilter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

object TelegramBot {
    suspend fun start(dependencies: BotDependencies): Unit = coroutineScope {
        val flows = FlowsUpdatesFilter()

        launchRestartable {
            flows.chatJoinRequestUpdateFlow.collect { update ->
                val userId = update.data.user.id.asTelegramUserId()
                val groupId = TelegramGroupId.createUnsafe(update.data.chat.id.chatId.long)

                when (val result = dependencies.checkIsUserAllowedToJoinUseCase.execute(userId, groupId)) {
                    is CheckIsUserAllowedToJoinUseCase.Result.Allowed -> {
                        val groupId = groupId.asChatId()
                        val userId = userId.asUserId()

                        dependencies.bot.approve(update.data)
                        dependencies.bot.promoteChatAdministrator(
                            chatId = groupId,
                            userId = userId,
                            canManageVideoChats = true,
                        )
                        dependencies.bot.setChatAdministratorCustomTitle(
                            chatId = groupId,
                            userId = userId,
                            customTitle = result.player.name.value,
                        )
                        dependencies.bot.send(
                            chatId = groupId,
                            entities = dependencies.strings.acceptedToTheClubChat(result.player),
                            linkPreviewOptions = LinkPreviewOptions.Disabled
                        )
                    }

                    CheckIsUserAllowedToJoinUseCase.Result.Denied -> {
                        println("denied")
                    }

                    is CheckIsUserAllowedToJoinUseCase.Result.Failure -> result.throwable.printStackTrace()
                }
            }
        }

        launchRestartable {
            flows.chatMemberUpdatesFlow
                .onEach { println(it) }
                .filter { event -> event.data.user.id.chatId == event.data.newChatMemberState.user.id.chatId }
                .filter { event ->
                    event.data.newChatMemberState is LeftChatMember &&
                        (event.data.oldChatMemberState is AdministratorChatMember || event.data.oldChatMemberState is MemberChatMember)
                }
                .collect {
                    val result = dependencies.getLinkedPlayerInChatUseCase.execute(
                        it.data.user.id.asTelegramUserId(),
                        TelegramGroupId.createUnsafe(it.data.chat.id.chatId.long),
                    )

                    when (result) {
                        is GetLinkedPlayerInChatUseCase.Result.Failure ->
                            result.throwable.printStackTrace()

                        GetLinkedPlayerInChatUseCase.Result.NotFound -> {
                            println("$it ignored, user not found in the system.")
                        }

                        is GetLinkedPlayerInChatUseCase.Result.Success -> {
                            dependencies.bot.send(
                                chatId = it.data.chat.id,
                                entities = dependencies.strings.leftClubChatMessage(result.player),
                                linkPreviewOptions = LinkPreviewOptions.Disabled,
                            )
                        }
                    }
                }
        }


        val fsm = dependencies.bot.buildBehaviourWithFSM<FSMState<*>>(
            flowUpdatesFilter = flows,
            defaultExceptionsHandler = { throwable ->
                throwable.printStackTrace()
            },
            onStateHandlingErrorHandler = { state, throwable ->
                throwable.printStackTrace()
                state
            }
        ) {
            handleBotPm(
                dependencies = dependencies,
                states = listOf(
                    CommonChatRulesState::class,
                    CommonClubRulesState::class,
                    CommonInitialState::class,
                    CommonPromptPlayerTagState::class,
                    CommonWantJoinChatState::class,
                    GuestMainMenuState::class,
                    GuestExploreClubsState::class,
                    MemberMainMenuState::class,
                    AdminMainMenuState::class,
                    AdminNotLinkedPlayersState::class,
                    AdminChooseClubState::class,
                    AdminChoosePlayersState::class,
                    AdminSendMessageState::class,
                ) as List<KClass<FSMState<FSMState.Dependencies>>>
            )
        }

        dependencies.bot.longPolling(flows).start()

        fsm.start(this)
    }
}


internal fun TelegramGroupId.asChatId(): ChatId = ChatId(RawChatId(value))
internal fun TelegramUserId.asUserId(): UserId = UserId(RawChatId(value))
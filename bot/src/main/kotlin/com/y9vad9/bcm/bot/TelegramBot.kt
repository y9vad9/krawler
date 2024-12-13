@file:Suppress("UNCHECKED_CAST")

package com.y9vad9.bcm.bot

import com.y9vad9.bcm.bot.ext.launchRestartable
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonChatRulesState
import com.y9vad9.bcm.bot.fsm.common.CommonClubRulesState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.bcm.bot.fsm.common.CommonWantJoinChatState
import com.y9vad9.bcm.bot.fsm.guest.GuestExploreClubsState
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.bcm.bot.fsm.member.MemberMainMenuState
import com.y9vad9.bcm.bot.handler.handleBotPm
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithFSM
import dev.inmo.tgbotapi.types.chat.PublicChat
import dev.inmo.tgbotapi.types.chat.member.LeftChatMember
import dev.inmo.tgbotapi.types.chat.member.MemberChatMember
import dev.inmo.tgbotapi.types.commands.BotCommandScope.Companion.ChatMember
import dev.inmo.tgbotapi.updateshandlers.FlowsUpdatesFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

object TelegramBot {
    suspend fun start(dependencies: BotDependencies): Unit = coroutineScope {
        CoroutineScope(Dispatchers.IO)
        val flows = FlowsUpdatesFilter()

        launchRestartable {
            flows.chatMemberUpdatesFlow
                .filter { event -> event.data.user.id.chatId == event.data.newChatMemberState.user.id.chatId }
                .filter { event ->
                    event.data.oldChatMemberState is LeftChatMember &&
                        event.data.newChatMemberState is MemberChatMember
                }
                .map { event -> ChatMember((event.data.chat as PublicChat).id, event.data.user.id) }
                .collect {
                    println(it)
                }
        }


        dependencies.bot.buildBehaviourWithFSM<FSMState<*, *>>(
            flowUpdatesFilter = flows
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
                ) as List<KClass<FSMState<*, FSMState.Dependencies>>>
            )
        }.start().join()
    }
}
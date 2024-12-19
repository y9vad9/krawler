package com.y9vad9.starix.bot

import com.y9vad9.starix.bot.fsm.admin.AdminChooseClubState
import com.y9vad9.starix.bot.fsm.admin.AdminChoosePlayersState
import com.y9vad9.starix.bot.fsm.admin.AdminNotLinkedPlayersState
import com.y9vad9.starix.bot.fsm.admin.AdminSendMessageState
import com.y9vad9.starix.bot.fsm.common.*
import com.y9vad9.starix.bot.fsm.guest.GuestExploreClubsState
import com.y9vad9.starix.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.starix.bot.fsm.member.MemberMainMenuState
import com.y9vad9.starix.core.system.usecase.CheckIsUserAllowedToJoinUseCase
import com.y9vad9.starix.core.system.usecase.GetLinkedPlayerInChatUseCase

interface BotDependencies :
    CommonChatRulesState.Dependencies,
    CommonClubRulesState.Dependencies,
    CommonInitialState.Dependencies,
    CommonPromptPlayerTagState.Dependencies,
    CommonWantJoinChatState.Dependencies,
    GuestExploreClubsState.Dependencies,
    GuestMainMenuState.Dependencies,
    MemberMainMenuState.Dependencies,
    AdminChooseClubState.Dependencies,
    AdminChoosePlayersState.Dependencies,
    AdminNotLinkedPlayersState.Dependencies,
    AdminSendMessageState.Dependencies {
    val checkIsUserAllowedToJoinUseCase: CheckIsUserAllowedToJoinUseCase
    val getLinkedPlayerInChatUseCase: GetLinkedPlayerInChatUseCase
}
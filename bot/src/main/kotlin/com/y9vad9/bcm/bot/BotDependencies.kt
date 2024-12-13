package com.y9vad9.bcm.bot

import com.y9vad9.bcm.bot.fsm.common.CommonChatRulesState
import com.y9vad9.bcm.bot.fsm.common.CommonClubRulesState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.common.CommonPromptPlayerTagState
import com.y9vad9.bcm.bot.fsm.common.CommonWantJoinChatState
import com.y9vad9.bcm.bot.fsm.guest.GuestExploreClubsState
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState

interface BotDependencies : CommonChatRulesState.Dependencies,
    CommonClubRulesState.Dependencies,
    CommonInitialState.Dependencies,
    CommonPromptPlayerTagState.Dependencies,
    CommonWantJoinChatState.Dependencies,
    GuestExploreClubsState.Dependencies,
    GuestMainMenuState.Dependencies
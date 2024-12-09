package com.y9vad9.bcm.bot.fsm.guest

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.createLoggingMessage
import com.y9vad9.bcm.domain.usecase.CheckClubsAvailabilityUseCase
import com.y9vad9.bcm.domain.usecase.GetAllowedClubsUseCase
import com.y9vad9.bcm.localization.Strings
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
sealed interface GuestFSMState<I : O, O : State, D : FSMState.Dependencies> : FSMState<I, O, D>
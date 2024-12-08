package com.y9vad9.gamerslegion.telegram.state

import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.types.IdChatIdentifier
import kotlinx.serialization.Serializable

@Serializable
sealed class FSMState : State {
    abstract override val context: IdChatIdentifier

    data class Initial(
        override val context: IdChatIdentifier,
    ) : FSMState()

    data class ProvidePlayerTag(
        override val context: IdChatIdentifier,
    ) : FSMState()

    data class ShouldAcceptClubRules(
        override val context: IdChatIdentifier,
    ) : FSMState()

    data class ShouldAcceptChatRules(
        override val context: IdChatIdentifier,
    ) : FSMState()

    data class ShouldReceiveUpdates(
        override val context: IdChatIdentifier,
    ) : FSMState()

    sealed class Admin : FSMState() {
        data class Initial(override val context: IdChatIdentifier) : Admin()
    }

    sealed class Member : FSMState() {
        data class Initial(override val context: IdChatIdentifier) : Member()
    }
}
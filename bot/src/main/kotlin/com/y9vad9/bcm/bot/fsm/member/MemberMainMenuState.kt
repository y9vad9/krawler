package com.y9vad9.bcm.bot.fsm.member

import com.y9vad9.bcm.bot.fsm.FSMState
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import kotlinx.serialization.Serializable

@Serializable
data class MemberMainMenuState(override val context: IdChatIdentifier) : MemberFSMState<MemberMainMenuState, State, MemberMainMenuState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *, *>,
        dependencies: Dependencies,
    ): State? = with(dependencies) {
        bot.send(
            chatId = context,
            text = strings.youAreInMemberMenuMessage,
            replyMarkup = ReplyKeyboardRemove(),
        )

        this@MemberMainMenuState
    }

    override suspend fun BehaviourContextWithFSM<in State>.process(
        dependencies: Dependencies,
        state: MemberMainMenuState,
    ): State? {
        return this@MemberMainMenuState
    }

    interface Dependencies : FSMState.Dependencies
}
package com.y9vad9.bcm.bot.fsm.common

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState.Dependencies
import com.y9vad9.bcm.bot.fsm.guest.GuestMainMenuState
import com.y9vad9.bcm.bot.fsm.member.MemberMainMenuState
import com.y9vad9.bcm.core.user.usecase.CheckUserStatusUseCase
import dev.inmo.micro_utils.fsm.common.State
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.types.IdChatIdentifier
import kotlinx.serialization.Serializable

@Serializable
data class CommonInitialState(override val context: IdChatIdentifier) : CommonFSMState<CommonInitialState, Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*, *>,
        dependencies: Dependencies,
    ): FSMState<*, *>? = this@CommonInitialState

    /**
     * Handles the `/start` command in private messages.
     *
     * The `CommonInitialState` determines the user's status (Admin, Member, or Guest) and initiates
     * the appropriate state to handle it. This ensures that users
     * are guided through actions and functionality relevant to their role.
     *
     * ## Behavior
     * - Uses the provided `checkUserStatus` use case to determine the user's status based on their
     *   Telegram ID.
     *
     * **Based on the user's status**:
     *   - **Admin**: Initializes the FSM flow for admin-related actions (`AdminMainMenuState`).
     *   - **Member**: Initializes the FSM flow for member-related actions (`MemberMainMenuState`).
     *   - **Guest**: Initializes the default FSM flow for guests (`GuestMainMenuState`).
     *
     * This handler ensures that every user is directed to the correct flow to match their role in the system.
     */
    override suspend fun BehaviourContextWithFSM<in FSMState<*, *>>.process(
        dependencies: Dependencies,
        state: CommonInitialState,
    ): FSMState<*, *>? = with(dependencies) {
        when (checkUserStatus.execute(context.asTelegramUserId())) {
            is CheckUserStatusUseCase.Result.Admin ->
                TODO()
            is CheckUserStatusUseCase.Result.Member ->
                MemberMainMenuState(context)
            CheckUserStatusUseCase.Result.Guest ->
                GuestMainMenuState(context)
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val checkUserStatus: CheckUserStatusUseCase
    }
}
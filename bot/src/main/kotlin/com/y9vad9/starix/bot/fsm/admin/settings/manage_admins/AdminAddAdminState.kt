package com.y9vad9.starix.bot.fsm.admin.settings.manage_admins

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.admin.AdminMainMenuState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.admin.AddAdminUseCase
import com.y9vad9.bcm.core.system.usecase.settings.admin.club.admin.GetNonAdminsListUseCase
import com.y9vad9.starix.localization.Strings
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

@SerialName("AdminAddAdminState")
@Serializable
data class AdminAddAdminState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
) : FSMState<AdminAddAdminState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        getNonAdmins(strings) { members ->
            bot.send(
                chatId = context,
                text = strings.admin.settings.chooseNewAdminMessage,
                replyMarkup = replyKeyboard {
                    members.chunked(2).forEach { subList ->
                        row {
                            subList.forEach { sub ->
                                simpleButton(sub.name.value)
                            }
                        }
                    }
                    row(simpleReplyButton(strings.goBackChoice))
                },
            )
            this@AdminAddAdminState
        }
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val text = waitText().first().text

        if (text == strings.goBackChoice) {
            return@with AdminViewAdminsState(context, clubTag)
        }

        getNonAdmins(strings) { members ->
            val member = members.firstOrNull {
                it.name.value == text
            } ?: return@getNonAdmins run {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminAddAdminState
            }

            when (val result = addAdmin.execute(clubTag, member.tag)) {
                is AddAdminUseCase.Result.Failure -> {
                    logAndProvideMessage(this@AdminAddAdminState, result.error)
                    this@AdminAddAdminState
                }

                AddAdminUseCase.Result.Success -> {
                    bot.send(
                        chatId = context,
                        entities = strings.admin.settings.adminWasAddedMessage(member),
                    )
                    this@AdminAddAdminState
                }

                AddAdminUseCase.Result.UserNotLinkedAccount -> {
                    bot.send(
                        chatId = context,
                        text = strings.accountIsNotLinked,
                    )
                    this@AdminAddAdminState
                }
            }
        }
    }

    private suspend fun Dependencies.getNonAdmins(
        strings: Strings,
        onSuccess: suspend (List<ClubMember>) -> FSMState<*>,
    ): FSMState<*> {
        return when (val result = getNonAdminsList.execute(clubTag)) {
            is GetNonAdminsListUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminAddAdminState, result.error)
                AdminMainMenuState(context)
            }

            is GetNonAdminsListUseCase.Result.Success -> {
                onSuccess(result.players)
            }

            GetNonAdminsListUseCase.Result.ClubNotFound -> {
                bot.send(
                    chatId = context,
                    text = strings.clubNotFoundMessage,
                )
                AdminMainMenuState(context)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val getNonAdminsList: GetNonAdminsListUseCase
        val addAdmin: AddAdminUseCase
    }
}
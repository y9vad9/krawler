package com.y9vad9.starix.bot.fsm.admin.settings.club_settings.player_linkage

import com.y9vad9.bcm.bot.ext.asTelegramUserId
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.common.CommonInitialState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.bcm.bot.fsm.logAndProvideMessage
import com.y9vad9.bcm.core.brawlstars.entity.club.ClubMember
import com.y9vad9.bcm.core.brawlstars.entity.club.value.ClubTag
import com.y9vad9.bcm.core.system.usecase.user.UnlinkPlayerUseCase
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

@SerialName("AdminManagePlayerLinkageSettingState")
@Serializable
data class AdminManagePlayerLinkageSettingState(
    override val context: IdChatIdentifier,
    val clubTag: ClubTag,
    val member: ClubMember,
    val isLinked: Boolean,
) : FSMState<AdminManagePlayerLinkageSettingState.Dependencies> {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.admin.settings.playerManageLinkageMessage(isLinked, member),
            replyMarkup = replyKeyboard {
                row {
                    if (isLinked)
                        simpleButton(strings.admin.settings.unlinkChoice)
                    else simpleButton(strings.admin.settings.linkChoice)
                }
                row(simpleReplyButton(strings.goBackChoice))
            },
        )
        this@AdminManagePlayerLinkageSettingState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (waitText().first().text) {
            strings.goBackChoice -> AdminPlayersLinkageSettingState(context, clubTag)
            strings.admin.settings.unlinkChoice -> unlinkPlayer(dependencies, strings)
            strings.admin.settings.linkChoice -> TODO()
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@AdminManagePlayerLinkageSettingState
            }
        }
    }

    private suspend fun unlinkPlayer(
        dependencies: Dependencies,
        strings: Strings,
    ): FSMState<*> = with(dependencies) {
        when (val result = unlinkPlayerUseCase.execute(context.asTelegramUserId(), member.tag)) {
            is UnlinkPlayerUseCase.Result.Failure -> {
                logAndProvideMessage(this@AdminManagePlayerLinkageSettingState, result.throwable)
                AdminPlayersLinkageSettingState(context, clubTag)
            }

            UnlinkPlayerUseCase.Result.NoAccess -> {
                bot.send(
                    chatId = context,
                    text = strings.noPermissionMessage,
                )
                CommonInitialState(context)
            }

            UnlinkPlayerUseCase.Result.NotLinked -> {
                bot.send(
                    chatId = context,
                    text = strings.playerNotLinkedMessage,
                )
                AdminPlayersLinkageSettingState(context, clubTag)
            }

            UnlinkPlayerUseCase.Result.Success -> AdminManagePlayerLinkageSettingState(
                context, clubTag, member, false,
            )
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val unlinkPlayerUseCase: UnlinkPlayerUseCase
    }
}
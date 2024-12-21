package com.y9vad9.starix.bot.fsm.common

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.components.language_picker.LanguagePickerComponentState
import com.y9vad9.starix.bot.fsm.components.timezone_picker.TimeZonePickerComponentState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.fsm.member.MemberFSMState
import com.y9vad9.starix.bot.fsm.member.MemberMainMenuState
import com.y9vad9.starix.core.system.entity.value.LanguageCode
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.types.message.textsources.pre
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneId

@SerialName("GuestMainMenuState")
@Serializable
data class CommonSettingsMenuState(
    override val context: IdChatIdentifier,
    private val newLanguageCode: LanguageCode? = null,
    private val newTimeZone: String? = null,
    private val callback: Callback,
) : CommonFSMState<CommonSettingsMenuState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {

        if (newLanguageCode != null) {
            val strings = stringsProvider.setStrings(context, newLanguageCode)
            bot.send(
                chatId = context,
                text = strings.successfullyChangedSettingMessage,
            )
        }

        val strings = getCurrentStrings(context)

        if (newTimeZone != null) {
            timeZoneProvider.setTimeZone(context, ZoneId.of(newTimeZone))
            bot.send(
                chatId = context,
                text = strings.successfullyChangedSettingMessage,
            )
        }

        bot.send(
            chatId = context,
            text = strings.youAreInGeneralSettingMessage,
            replyMarkup = replyKeyboard {
                row {
                    simpleButton(strings.member.settings.changeLocaleChoice)
                    simpleButton(strings.member.settings.changeTimeZoneChoice)
                }

                row(simpleReplyButton(strings.goBackChoice))
            },
        )

        this@CommonSettingsMenuState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        return@with when (waitText().first().text) {
            strings.goBackChoice -> callback.navigateBack(context)
            strings.member.settings.changeLocaleChoice -> LanguagePickerComponentState(
                context = context,
                callback = LanguagePickerComponentStateCallback(callback),
            )
            strings.member.settings.changeTimeZoneChoice -> TimeZonePickerComponentState(
                context = context,
                callback = TimeZonePickerComponentStateCallback(callback),
            )
            else -> {
                bot.send(
                    chatId = context,
                    text = strings.invalidChoiceMessage,
                )
                this@CommonSettingsMenuState
            }
        }
    }

    interface Dependencies : FSMState.Dependencies

    interface Callback {
        fun navigateBack(context: IdChatIdentifier): FSMState<*>
    }

    @SerialName("MemberSettingsMenuState.LanguagePickerComponentStateCallback")
    @Serializable
    private data class LanguagePickerComponentStateCallback(
        val prevCallback: Callback,
    ) : LanguagePickerComponentState.Callback {
        override fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return CommonSettingsMenuState(context, callback = prevCallback)
        }

        override fun navigateForward(
            context: IdChatIdentifier,
            code: LanguageCode,
        ): FSMState<*> {
            return CommonSettingsMenuState(context, newLanguageCode = code, callback = prevCallback)
        }
    }

    @SerialName("MemberSettingsMenuState.TimeZonePickerComponentStateCallback")
    @Serializable
    private data class TimeZonePickerComponentStateCallback(
        val prevCallback: Callback,
    ) : TimeZonePickerComponentState.Callback {
        override suspend fun navigateBack(context: IdChatIdentifier): FSMState<*> {
            return CommonSettingsMenuState(context, callback = prevCallback)
        }

        override suspend fun navigateForward(context: IdChatIdentifier, zoneId: ZoneId): FSMState<*> {
            return CommonSettingsMenuState(
                context = context,
                newTimeZone = zoneId.id,
                callback = prevCallback,
            )
        }
    }
}
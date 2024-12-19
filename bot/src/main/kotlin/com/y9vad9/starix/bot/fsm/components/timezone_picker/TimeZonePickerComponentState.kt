package com.y9vad9.starix.bot.fsm.components.timezone_picker

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.common.CommonFSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import com.y9vad9.starix.bot.provider.TimeZoneProvider
import com.y9vad9.starix.bot.provider.getTimeZonesWithCountries
import com.y9vad9.starix.bot.provider.toOffsetString
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneId

@SerialName("TimeZonePickerComponentState")
@Serializable
data class TimeZonePickerComponentState(
    override val context: IdChatIdentifier,
    val callback: com.y9vad9.starix.bot.fsm.components.timezone_picker.TimeZonePickerComponentState.Callback,
) : CommonFSMState<com.y9vad9.starix.bot.fsm.components.timezone_picker.TimeZonePickerComponentState.Dependencies> {
    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: _root_ide_package_.com.y9vad9.starix.bot.fsm.components.timezone_picker.TimeZonePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        bot.send(
            chatId = context,
            entities = strings.components.timeZonePicker.pickTimeZone(
                timeZoneProvider.getTimeZone(context).toOffsetString()
            ),
            replyMarkup = replyKeyboard {
                TimeZoneProvider.getTimeZonesWithCountries().entries.forEach { (_, representative) ->
                    row {
                        simpleButton(representative)
                    }
                }
                row {
                    simpleButton(strings.goBackChoice)
                }
            }
        )
        this@TimeZonePickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: _root_ide_package_.com.y9vad9.starix.bot.fsm.components.timezone_picker.TimeZonePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val input = waitText().first().text) {
            strings.goBackChoice -> callback.navigateBack(context)

            else -> {
                val selectedTimeZone = TimeZoneProvider.getTimeZonesWithCountries()
                    .entries
                    .firstOrNull { it.value == input }
                    ?.key
                    ?: run {
                        bot.send(
                            chatId = context,
                            text = strings.invalidChoiceMessage,
                        )
                        return@with this@TimeZonePickerComponentState
                    }

                callback.navigateForward(context, selectedTimeZone)
            }
        }
    }

    interface Dependencies : FSMState.Dependencies {
        val timeZoneProvider: TimeZoneProvider
    }

    interface Callback {
        suspend fun navigateBack(context: IdChatIdentifier): FSMState<*>
        suspend fun navigateForward(context: IdChatIdentifier, zoneId: ZoneId): FSMState<*>
    }
}
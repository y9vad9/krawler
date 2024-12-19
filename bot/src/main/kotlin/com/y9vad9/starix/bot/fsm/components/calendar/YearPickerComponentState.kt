package com.y9vad9.starix.bot.fsm.components.calendar

import com.y9vad9.starix.bot.fsm.FSMState
import com.y9vad9.starix.bot.fsm.getCurrentStrings
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextWithFSM
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@SerialName("YearPickerComponentState")
@Serializable
data class YearPickerComponentState(
    override val context: IdChatIdentifier,
    override val callback: DatePickerComponentState.Callback,
    override val canPickPast: Boolean,
    override val shouldPickTime: Boolean,
) : DatePickerComponentState {
    private companion object {
        const val MIN_YEAR = 2024
    }

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val calendar = calendarProvider.getCalendar()

        bot.send(
            chatId = context,
            entities = strings.components.calendar.pickMonthMessage,
            replyMarkup = replyKeyboard {
                row(SimpleKeyboardButton(strings.components.calendar.currentYearChoice))

                val currentYear = calendar.get(Calendar.YEAR)

                row {
                    val startYear = if (canPickPast) MIN_YEAR else currentYear
                    val range = startYear..startYear + 2

                    range.forEach { year ->
                        simpleButton(year.toString())
                    }
                }
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@YearPickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val calendar = calendarProvider.getCalendar()

        when (val input = waitText().first().text) {
            strings.goBackChoice -> callback.navigateBack(context)
            strings.components.calendar.currentYearChoice ->
                MonthPickerComponentState(
                    context = context,
                    callback = callback,
                    year = calendar.get(Calendar.YEAR),
                    shouldPickTime = shouldPickTime,
                    canPickPast = canPickPast,
                )

            else -> {
                val year = input.toIntOrNull()
                    ?: run {
                        bot.send(
                            chatId = context,
                            text = strings.invalidChoiceMessage,
                        )
                        return@with this@YearPickerComponentState
                    }
                MonthPickerComponentState(
                    context = context,
                    shouldPickTime = shouldPickTime,
                    canPickPast = canPickPast,
                    callback = callback,
                    year = year,
                )
            }
        }
    }

}
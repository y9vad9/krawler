package com.y9vad9.starix.bot.fsm.components.calendar

import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
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
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@SerialName("MonthPickerComponentState")
@Serializable
data class MonthPickerComponentState(
    override val context: IdChatIdentifier,
    override val callback: DatePickerComponentState.Callback,
    override val canPickPast: Boolean,
    override val shouldPickTime: Boolean,
    val year: Int,
) : DatePickerComponentState {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val calendar = calendarProvider.getCalendar()
        val currentLocale = strings.locale

        bot.send(
            chatId = context,
            entities = strings.components.calendar.pickMonthMessage,
            replyMarkup = replyKeyboard {
                row(SimpleKeyboardButton(strings.components.calendar.currentMonthChoice))
                getMonthsLocalized(
                    calendar = calendar,
                    locale = currentLocale,
                ).values.chunked(4).forEach { months ->
                    row {
                        months.forEach { month ->
                            simpleButton(month)
                        }
                    }
                }

                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@MonthPickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val calendar = calendarProvider.getCalendar()

        when (val input = waitText().first().text) {
            strings.goBackChoice -> YearPickerComponentState(
                context, callback, canPickPast, shouldPickTime,
            )

            strings.components.calendar.currentMonthChoice ->
                DayPickerComponentState(
                    context = context,
                    canPickPast = canPickPast,
                    shouldPickTime = shouldPickTime,
                    callback = callback,
                    year = year,
                    month = calendar.get(Calendar.MONTH),
                )

            else -> {
                val month = getMonthsLocalized(calendar, strings.locale).entries.firstOrNull {
                    it.value == input
                }?.key?.ordinal ?: run {
                    bot.send(
                        chatId = context,
                        text = strings.invalidChoiceMessage,
                    )
                    return@with this@MonthPickerComponentState
                }

                DayPickerComponentState(
                    context = context,
                    shouldPickTime = shouldPickTime,
                    canPickPast = canPickPast,
                    callback = callback,
                    year = year,
                    month = month,
                )
            }
        }
    }

    private fun getMonthsLocalized(
        calendar: Calendar,
        locale: Locale,
    ): Map<Month, String> {
        @Suppress("UNCHECKED_CAST")
        return Month.entries.associateWith { month ->
            if (canPickPast || calendar.get(Calendar.MONTH) <= month.ordinal)
                month.getDisplayName(TextStyle.FULL, locale)
            else null
        }.filterValues { it != null } as Map<Month, String>
    }

}
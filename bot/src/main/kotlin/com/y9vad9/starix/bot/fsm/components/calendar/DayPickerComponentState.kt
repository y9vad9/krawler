package com.y9vad9.starix.bot.fsm.components.calendar

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.bcm.bot.fsm.FSMState
import com.y9vad9.bcm.bot.fsm.getCurrentStrings
import com.y9vad9.starix.foundation.validation.annotations.ValidationDelicateApi
import com.y9vad9.starix.foundation.validation.createUnsafe
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
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.*

@SerialName("DayPickerComponentState")
@Serializable
data class DayPickerComponentState(
    override val context: IdChatIdentifier,
    override val callback: DatePickerComponentState.Callback,
    override val canPickPast: Boolean,
    override val shouldPickTime: Boolean,
    val year: Int,
    val month: Int,
) : DatePickerComponentState {

    override suspend fun BehaviourContext.before(
        previousState: FSMState<*>,
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)
        val currentLocale = strings.locale

        bot.send(
            chatId = context,
            entities = strings.components.calendar.pickMonthMessage,
            replyMarkup = replyKeyboard {
                row(SimpleKeyboardButton(strings.components.calendar.currentDayChoice))
                row {
                    getDaysInMonthWithLocale(month, year, currentLocale).values.chunked(4).forEach { days ->
                        row {
                            days.forEach { day ->
                                simpleButton(day)
                            }
                        }
                    }
                }
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@DayPickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        val calendar = calendarProvider.getCalendar()

        when (val input = waitText().first().text) {
            strings.goBackChoice -> MonthPickerComponentState(
                context, callback, canPickPast, shouldPickTime, year,
            )

            strings.components.calendar.currentDayChoice -> {
                if (shouldPickTime) {
                    TimePickerComponentState(
                        context = context,
                        callback = callback,
                        canPickPast = canPickPast,
                        year = year,
                        month = month,
                        day = calendar.get(Calendar.DAY_OF_MONTH),
                    )
                } else {
                    val date = LocalDate.of(year, month, calendar.get(Calendar.DAY_OF_MONTH))

                    @OptIn(ValidationDelicateApi::class)
                    callback.navigateForward(
                        context,
                        UnixTime.createUnsafe(date.atStartOfDay().atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    )
                }
            }

            else -> {
                val day = getDaysInMonthWithLocale(month, year, strings.locale)
                    .entries
                    .firstOrNull { it.value == input }
                    ?.key
                    ?: run {
                        bot.send(
                            chatId = context,
                            text = strings.invalidChoiceMessage,
                        )
                        return@with this@DayPickerComponentState
                    }

                if (shouldPickTime) {
                    TimePickerComponentState(
                        context = context,
                        callback = callback,
                        year = year,
                        month = month,
                        day = day,
                        canPickPast = canPickPast,
                    )
                } else {
                    // TODO
                    val date = LocalDate.of(year, month, day)

                    @OptIn(ValidationDelicateApi::class)
                    callback.navigateForward(
                        context,
                        UnixTime.createUnsafe(date.atStartOfDay().atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                    )
                }
            }
        }
    }

    fun getDaysInMonthWithLocale(month: Int, year: Int, locale: Locale): Map<Int, String> {
        val result = mutableMapOf<Int, String>()
        val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()

        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            result[day] = dayOfWeek
        }

        return result
    }

}
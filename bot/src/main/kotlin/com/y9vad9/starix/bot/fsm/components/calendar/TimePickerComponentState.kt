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
import dev.inmo.tgbotapi.types.buttons.reply.simpleReplyButton
import dev.inmo.tgbotapi.utils.row
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SerialName("TimePickerComponentState")
@Serializable
data class TimePickerComponentState(
    override val context: IdChatIdentifier,
    override val callback: DatePickerComponentState.Callback,
    override val canPickPast: Boolean,
    val year: Int,
    val month: Int,
    val day: Int,
) : DatePickerComponentState {
    override val shouldPickTime: Boolean = true

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
                row {
                    getTimeRepresentation(
                        stepMinutes = 30,
                        locale = currentLocale,
                        currentCalendar = calendar,
                    ).entries.chunked(4).forEach { entries ->
                        row {
                            entries.forEach { (_, representative) ->
                                simpleButton(representative)
                            }
                        }
                    }
                }
                row(simpleReplyButton(strings.goBackChoice))
            }
        )
        this@TimePickerComponentState
    }

    override suspend fun BehaviourContextWithFSM<in FSMState<*>>.process(
        dependencies: DatePickerComponentState.Dependencies,
    ): FSMState<*> = with(dependencies) {
        val strings = getCurrentStrings(context)

        when (val input = waitText().first().text) {
            strings.goBackChoice -> DayPickerComponentState(
                context, callback, canPickPast, shouldPickTime, year, month,
            )

            else -> {
                val time = parseTime(input, strings.locale)
                    ?: run {
                        bot.send(
                            chatId = context,
                            text = strings.invalidChoiceMessage,
                        )
                        return@with this@TimePickerComponentState
                    }

                val dateTime = ZonedDateTime.of(LocalDate.of(year, month, day), time, ZoneOffset.UTC)

                @OptIn(ValidationDelicateApi::class)
                callback.navigateForward(context, UnixTime.createUnsafe(dateTime.toInstant().toEpochMilli()))
            }
        }
    }

    fun getTimeRepresentation(
        stepMinutes: Int = 30,
        locale: Locale,
        currentCalendar: Calendar,
    ): Map<LocalTime, String> {
        val start =
            if (currentCalendar.get(Calendar.YEAR) == year && currentCalendar.get(Calendar.MONTH) == month && currentCalendar.get(
                    Calendar.DAY_OF_MONTH
                ) == day
            ) {
                LocalTime.of(currentCalendar.get(Calendar.HOUR_OF_DAY) + 1, 0)
            } else {
                LocalTime.MIDNIGHT
            }

        //val start = LocalTime.of(0, 0) // Midnight
        val end = LocalTime.of(23, 59) // End of the day

        val formatter = DateTimeFormatter.ofPattern("hh:mm a", locale)

        return buildMap {
            var currentTime = start
            while (currentTime.isBefore(LocalTime.MAX)) {
                put(currentTime, currentTime.format(formatter))
                currentTime = currentTime.plusMinutes(stepMinutes.toLong())
            }
        }
    }

    private fun parseTime(input: String, locale: Locale): LocalTime? {
        val patterns = listOf(
            "hh:mm a",     // "4:30 AM", "3:20 PM"
            "h:mm a",      // "4 AM", "3:20 PM"
            "HH:mm",       // "04:30"
            "h:mm"         // "4:30"
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern, locale)
                return LocalTime.parse(input, formatter) // Try parsing the input with each pattern
            } catch (e: Exception) {
                // Ignore the exception and try the next pattern
            }
        }
        return null // Return null if no pattern matched
    }
}
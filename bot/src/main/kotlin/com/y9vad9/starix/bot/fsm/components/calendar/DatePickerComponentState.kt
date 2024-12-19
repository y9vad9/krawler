package com.y9vad9.starix.bot.fsm.components.calendar

import com.y9vad9.starix.foundation.time.UnixTime
import com.y9vad9.bcm.bot.fsm.FSMState
import dev.inmo.tgbotapi.types.IdChatIdentifier
import java.util.*

// TODO: user-specific time zone
sealed interface DatePickerComponentState : FSMState<DatePickerComponentState.Dependencies> {
    val canPickPast: Boolean
    val shouldPickTime: Boolean
    val callback: Callback

    interface Dependencies : FSMState.Dependencies {
        val calendarProvider: CalendarProvider
    }

    interface Callback {
        fun navigateBack(context: IdChatIdentifier): FSMState<*>
        fun navigateForward(context: IdChatIdentifier, dateTime: UnixTime): FSMState<*>
    }
}

interface CalendarProvider {
    fun getCalendar(): Calendar
}
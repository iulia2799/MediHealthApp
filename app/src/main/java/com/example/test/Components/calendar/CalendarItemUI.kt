package com.example.test.Components.calendar

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalendarItemUI(
    val currentDate: Date, val week: List<Date>
) {
    val startDate = week.first()
    val endDate = week.last()

    data class Date(
        val date: LocalDate, val isSelected: Boolean = false, val isToday: Boolean = false
    ) {
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }
}

package com.example.test.Components.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

class WeeklyDataSource {
    val today: LocalDate
        get() {
            return LocalDate.now()
        }


    fun getData(startDate: LocalDate = today, lastSelectedDate: LocalDate): CalendarItemUI {
        val firstDayOfWeek = startDate.with(DayOfWeek.MONDAY)
        val endDayOfWeek = firstDayOfWeek.plusDays(7)
        val visibleDates = getDatesBetween(firstDayOfWeek, endDayOfWeek)
        return toUiModel(visibleDates, lastSelectedDate)
    }

    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val numOfDays = ChronoUnit.DAYS.between(startDate, endDate)
        return Stream.iterate(startDate) { date ->
            date.plusDays(1)
        }.limit(numOfDays).collect(Collectors.toList())
    }

    private fun toUiModel(
        dateList: List<LocalDate>, lastSelectedDate: LocalDate
    ): CalendarItemUI {
        return CalendarItemUI(
            currentDate = toItemUiModel(lastSelectedDate, true),
            week = dateList.map {
                toItemUiModel(it, it.isEqual(lastSelectedDate))
            },
        )
    }

    private fun toItemUiModel(date: LocalDate, isSelectedDate: Boolean) = CalendarItemUI.Date(
        isSelected = isSelectedDate,
        isToday = date.isEqual(today),
        date = date,
    )
}
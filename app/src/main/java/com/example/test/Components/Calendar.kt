package com.example.test.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendar(yearMonth: YearMonth, monthlySchedule : List<String>) {
    Column {
        // Display the month
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            modifier = Modifier.fillMaxWidth()
        )
        // Display the days of the week
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f))
            }
        }
        // Calculate the number of days in the month
        val daysInMonth = yearMonth.lengthOfMonth()
        // Calculate the day of the week for the first day of the month
        val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7
        // Create a grid representing the days of the month
        for (week in 0 until 6) { // 6 weeks to cover all possibilities
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0 until 7) {
                    val dayOfMonth = week * 7 + dayOfWeek - firstDayOfWeek + 1
                    if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                        Text(text = "$dayOfMonth", modifier = Modifier.weight(1f), color = Color.Blue)
                    } else {
                        Text(text = "", modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
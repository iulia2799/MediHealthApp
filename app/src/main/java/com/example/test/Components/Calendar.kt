package com.example.test.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendar(yearMonth: YearMonth) {
    Column(
        modifier = Modifier
            .background(universalBackground, shape = RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        // Display the month
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Center,
            fontFamily = jejugothicFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        // Display the days of the week
        Row(modifier = Modifier
            .fillMaxWidth().padding(10.dp)) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day, modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    fontFamily = jejugothicFamily,

                )
            }
        }
        // Calculate the number of days in the month
        val daysInMonth = yearMonth.lengthOfMonth()
        // Calculate the day of the week for the first day of the month
        val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7
        // Create a grid representing the days of the month
        for (week in 0 until 6) { // 6 weeks to cover all possibilities
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
                for (dayOfWeek in 0 until 7) {
                    val dayOfMonth = week * 7 + dayOfWeek - firstDayOfWeek + 2
                    if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                        Text(
                            text = "$dayOfMonth", modifier = Modifier
                                .weight(1f)
                                .background(
                                    universalPrimary, RoundedCornerShape(
                                        10.dp
                                    )
                                ).padding(10.dp), textAlign = TextAlign.Center,
                            fontFamily = jejugothicFamily,
                        )
                    } else {
                        Text(text = "", modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
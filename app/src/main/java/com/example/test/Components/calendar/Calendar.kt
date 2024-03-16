package com.example.test.Components.calendar

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomCardViewDark
import com.example.test.Components.DefaultIconButton
import com.example.test.ui.theme.universalPrimary
import java.time.LocalDate


@Composable
fun TopCalendarBar(data: CalendarItemUI,onPrevClickListener: (LocalDate) -> Unit,
                   onNextClickListener: (LocalDate) -> Unit,) {
    val dayOfWeek = LocalDate.now().dayOfWeek
    val today = LocalDate.now().dayOfMonth
    val month = LocalDate.now().month
    val year = LocalDate.now().year
    Row {
        Text(
            text = "$dayOfWeek $today, $month $year",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = universalPrimary
        )
        DefaultIconButton(onClick = { onPrevClickListener(data.startDate.date) }, imageVector = Icons.Rounded.KeyboardArrowLeft, description = "Last Week")
        DefaultIconButton(onClick = { onNextClickListener(data.endDate.date) }, imageVector = Icons.Rounded.KeyboardArrowRight, description = "Next Week")
    }

}


@Composable
fun CustomCalendar(week: CalendarItemUI,onPrevClickListener: (LocalDate) -> Unit,
                   onNextClickListener: (LocalDate) -> Unit,onDateClickListener: (CalendarItemUI.Date) -> Unit,) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopCalendarBar(week,onPrevClickListener,onNextClickListener)
        LazyRow(modifier = Modifier.fillMaxWidth()){
            items(items = week.week) {
                CalendarItem(date = it,onDateClickListener)
            }
        }
    }
}

@Composable
fun CalendarItem(date: CalendarItemUI.Date, onDateClickListener: (CalendarItemUI.Date) -> Unit) {
    CustomCardViewDark(modifier = Modifier.padding(4.dp), isSelected = date.isSelected,
        content = {
            Column(modifier = Modifier
                .width(40.dp)
                .height(48.dp)
                .padding(4.dp).clickable { // making the element clickable, by adding 'clickable' modifier
                    Log.d("what", "WHAT")
                    onDateClickListener(date)
                }) {
                Text(
                    text = date.day,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = date.date.dayOfMonth.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        })
}

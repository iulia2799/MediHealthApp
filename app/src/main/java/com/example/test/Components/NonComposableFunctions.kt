package com.example.test.Components

import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    return formatter.format(Date(millis))
}
fun convertTimeToTimestamp(hour: Int, minutes: Int): Long {
    val hourStamp = hour * 60L * 60L
    val minuteStamp = minutes * 60L
    return hourStamp + minuteStamp
}
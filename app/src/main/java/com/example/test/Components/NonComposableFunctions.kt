package com.example.test.Components

import Models.Conversation
import Models.Doctor
import Models.Patient
import android.content.Context
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Calendar
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

fun zonedDateTimeToTimestampFirebase(dateTime: ZonedDateTime): Timestamp {
    return Timestamp(dateTime.toInstant().epochSecond, dateTime.nano)
}

fun <K> filterByField(map: Map<K, Doctor>, query: String): Map<K, Doctor> {
    return map.filterValues { data ->
        data.firstName.contains(query, ignoreCase = true) || data.lastName.contains(
            query, ignoreCase = true
        ) || "${data.firstName} ${data.lastName}".contains(query, ignoreCase = true)
    }
}

fun <K> filterByFieldP(map: Map<K, Patient>, query: String): Map<K, Patient> {
    return map.filterValues { data ->
        data.firstName.contains(query, ignoreCase = true) || data.lastName.contains(
            query, ignoreCase = true
        ) || "${data.firstName} ${data.lastName}".contains(query, ignoreCase = true)
    }
}

fun convertTimestampToDate(timestamp: Timestamp): Triple<String, Int, Int> {
    val format = SimpleDateFormat("dd/MM/yyyy")
    val dateString = format.format(timestamp.toDate())

    val calendar = Calendar.getInstance()
    calendar.time = timestamp.toDate()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return Triple(dateString, hour, minute)
}

fun convertDateToTimeStamp(hour: Int, minute: Int, dateString: String): Timestamp {
    val format = SimpleDateFormat("dd/MM/yyyy")
    var timestamp = Timestamp.now()
    try {
        val date: Date = format.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        timestamp = Timestamp(Date(calendar.timeInMillis))
        println("Firebase Timestamp: $timestamp")
    } catch (e: Exception) {
        println("Error converting to timestamp: $e")
    }
    return timestamp
}

fun convertDayStampToHourAndMinute(seconds: Long): Pair<Int, Int> {
    val hours = (seconds / (60 * 60)).toInt()
    val minutes = ((seconds % (60 * 60)) / 60).toInt()
    return Pair(hours, minutes)
}

fun convertDayMillisToHourAndMinute(millis: Long): Pair<Int, Int> {
    val hours = (millis / (1000 * 60 * 60)).toInt()
    val minutes = ((millis % (1000 * 60 * 60)) / (1000 * 60)).toInt()
    return Pair(hours, minutes)
}

fun TimeUnitToString(value: Int): String {
    return if (value < 10) {
        "0$value"
    } else {
        value.toString()
    }
}

fun getFromUserUids(convo: Conversation, context: Context): Pair<String,String> {
    var localStorage = LocalStorage(context)
    val ref = localStorage.getRef()
    val index = convo.userUids.indexOf(ref)
    return Pair(convo.userUids[index],convo.userNames[index])
}
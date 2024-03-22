package com.example.test.Components

import Models.Doctor
import Models.Patient
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
    return Timestamp(dateTime.toInstant().epochSecond,dateTime.nano)
}

fun <K> filterByField(map: Map<K, Doctor>, query: String): Map<K, Doctor> {
    return map.filterValues { data ->
        data.firstName.contains(query) || data.lastName.contains(query)
    }
}

fun <K> filterByFieldP(map: Map<K, Patient>, query: String): Map<K, Patient> {
    return map.filterValues { data ->
        data.firstName.contains(query) || data.lastName.contains(query)
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

fun convertDateToTimeStamp(hour: Int,minute:Int, dateString: String): Timestamp {
    val format = SimpleDateFormat("dd/MM/yyyy")
    var timestamp = Timestamp.now()
    try {
        val date: Date = format.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        timestamp = Timestamp(Date(calendar.timeInMillis))
        // Use the timestamp for Firebase operations
        println("Firebase Timestamp: $timestamp")
    } catch (e: Exception) {
        // Handle potential parsing errors or invalid dates
        println("Error converting to timestamp: $e")
        // Show an error message or use a default timestamp
    }
    return timestamp
}
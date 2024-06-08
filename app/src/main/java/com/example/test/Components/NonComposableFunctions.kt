package com.example.test.Components

import Models.Conversation
import Models.Doctor
import Models.Patient
import android.content.Context
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.time.ZoneOffset.UTC

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    return formatter.format(Date(millis))
}

fun convertMillisToExactDate(millis: Long): String {
    val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ROOT)
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
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    val dateString = format.format(timestamp.toDate())

    val calendar = Calendar.getInstance()
    calendar.time = timestamp.toDate()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return Triple(dateString, hour, minute)
}

fun convertDateToTimeStamp(hour: Int, minute: Int, dateString: String): Timestamp {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    var timestamp = Timestamp.now()
    try {
        val date: Date? = format.parse(dateString)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        timestamp = Timestamp(Date(calendar.timeInMillis))
        println("Firebase Timestamp: $timestamp")
    } catch (e: Exception) {
        println("Error converting to timestamp: $e")
    }
    return timestamp
}

fun convertTimeStampToHourAndMinute(seconds: Long): Pair<Int, Int> {
    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    return Pair(hours, minutes)
}


fun timeUnitToString(value: Int): String {
    return if (value < 10) {
        "0$value"
    } else {
        value.toString()
    }
}

fun getFromUserUids(convo: Conversation, context: Context): Pair<String, String> {
    val localStorage = LocalStorage(context)
    val ref = localStorage.getRef()
    val result = convo.userUids.find { value -> value != ref }
    val index = convo.userUids.indexOf(result)
    return Pair(convo.userUids[index], convo.userNames[index])
}

fun convertBooleanToResult(accepted: Boolean): String {
    return if (accepted) "Yes" else "No"
}

fun toTitleCase(value: String): String {
    return value.replace("_", " ")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}

fun makeDiscountedNumber(value: String, discount: String): String {
    return if (value.isEmpty()) "0.00"
    else if (discount.isEmpty()) value
    else {
        try {
            val discountedSum = value.toFloat() * discount.toFloat() / 100.0
            val result = value.toFloat() - discountedSum
            result.toString()
        } catch (exception: Exception) {
            "NAN"
        }
    }
}

fun convertToUtcDailySeconds(value: Long): Long {
    val midnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
    val current = midnight.plusSeconds(value)
    val utc = current.atZone(ZoneId.systemDefault()).toInstant().atZone(UTC)
        .toLocalDateTime()
    return (utc.hour * 3600 + utc.minute * 60 + utc.second).toLong()

}

fun deconvertToDailySeconds(value: Long): Long {
    val midnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
    val current = midnight.plusSeconds(value)
    val timezoned = current.atZone(UTC).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    return (timezoned.hour * 3600 + timezoned.minute * 60 + timezoned.second).toLong()
}
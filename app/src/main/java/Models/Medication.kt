package Models

import com.google.firebase.Timestamp

data class Medication(
    val doctorUid: String = "0",
    val patientUid: String = "0",
    val patientName: String = "",
    val doctorName: String = "",
    val frequency: String = "",
    val medicationName: String = "",
    val description: String = "",
    val pills: Int = 0,
    val days: Int = 0,
    val pillsPerPortion: Int = 0,
    val medType: Department = Department.NA,
    val alarms: List<Long> = emptyList(),
    val timestamp : Timestamp = Timestamp.now()
)

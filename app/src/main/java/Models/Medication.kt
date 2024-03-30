package Models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

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
    val medType: Department = Department.NA,
    val alarms: List<Long> = emptyList()
)

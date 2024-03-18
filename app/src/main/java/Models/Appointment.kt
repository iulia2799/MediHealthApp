package Models

import com.google.firebase.Timestamp


data class Appointment(
    var ref: String? = null,
    var doctorUid: String,
    var doctorName: String,
    var patientUid: String,
    var patientName: String,
    var date: Timestamp,
    var alocatedTime: Long = 15 * 60,
)

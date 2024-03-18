package Models

import com.google.firebase.Timestamp


data class Appointment(
    var ref: String? = null,
    var doctorUid: String ="",
    var doctorName: String ="",
    var patientUid: String ="",
    var patientName: String ="",
    var description: String = "",
    var date: Timestamp = Timestamp.now(),
    var alocatedTime: Long = 15 * 60,
)

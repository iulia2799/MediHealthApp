package Models

data class Appointment(
    val doctorUid: Long,
    val patientUid: Long,
    val date: java.util.Date
)

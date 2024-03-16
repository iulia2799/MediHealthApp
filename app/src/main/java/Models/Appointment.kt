package Models

import java.sql.Time

data class Appointment(
    var doctorUid: String,
    var doctorName: String,
    var patientUid: String,
    var patientName: String,
    var date: Time,
    var alocatedTime: Time,
)

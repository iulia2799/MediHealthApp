package Models

data class Appointment(
    var doctorUid: Long,
    var patientUid: Long,
    var date: Long,
    var alocatedTime: Long,
)

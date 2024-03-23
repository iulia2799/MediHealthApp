package Models

data class Medication(
    val uid: Long,
    val doctorUid: Long,
    val userUid: Long,
    val patientName: String,
    val doctorName: String,
    val frequency: String,
    val medicationName: String,
    val pills: Int,
    val days: Int,
    val medType: Department
)

package Models

data class ResultRecord(
    var patientRef: String = "",
    var patientName: String = "",
    var doctorRef: String = "",
    var doctorName: String = "",
    var fileRefStorageUrl: String = "",
    var optionalFiles: List<String> = emptyList(),
    var description: String = "",
    var unix: Long = System.currentTimeMillis()
)
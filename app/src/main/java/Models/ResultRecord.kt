package Models

data class ResultRecord(
    var patientRef: String,
    var patientName: String,
    var doctorRef: String,
    var doctorName: String,
    var fileRefStorage: String,
    var description: String,
) {
}
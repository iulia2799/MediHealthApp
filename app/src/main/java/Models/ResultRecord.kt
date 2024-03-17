package Models

data class ResultRecord(
    var patientRef: String,
    var doctorRef: String,
    var fileRefStorage: String,
    var description: String,
) {
}
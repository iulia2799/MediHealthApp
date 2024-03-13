package Models

data class Result(
    var patientRef: String,
    var doctorRef: String,
    var fileRefStorage: String,
    var description: String,
) {
}
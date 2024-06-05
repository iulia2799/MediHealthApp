package Models

data class Billing(
    var patientUid: String = "",
    var doctorUid: String = "",
    var patientName: String = "",
    var doctorName: String = "",
    var initialSum: Float = 0f,
    var coveredByInsurance: Boolean = false,
    var discount: Float = 0f,
    var finalSum: Float = 0f,
    var AccountNumber: String = "",
    var files: List<String> = emptyList(),
    var currency: String = "",
    var unix: Long = System.currentTimeMillis()
)

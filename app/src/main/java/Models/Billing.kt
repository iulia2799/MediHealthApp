package Models

import java.util.Currency
import java.util.Locale

data class Billing(
    var patientUid: String = "",
    var doctorUid: String = "",
    var patientName: String = "",
    var doctorName: String = "",
    var initialSum: Int = 0,
    var coveredByInsurance: Boolean = false,
    var discount: Int = 0,
    var finalSum: Int = 0,
    var AccountNumber: String = "",
    var files: List<String> = emptyList(),
    var currency: String = ""
)

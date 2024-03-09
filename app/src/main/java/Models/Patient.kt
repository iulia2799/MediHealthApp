package Models

import org.json.JSONObject

data class Patient(
    var uid: Long,
    var firstName: String,
    var lastName: String,
    var address: String,
    var phone: String,
    var email: String,
    var password: String,
    var age: Int,
    var doctorUid: Long,
    var history: JSONObject,
    var prescription: List<Medication>,
    var results: JSONObject
) {


}
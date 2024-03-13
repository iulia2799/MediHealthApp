package Models

import org.json.JSONObject

data class Patient(
    var uid: String,
    var firstName: String,
    var lastName: String,
    var address: String,
    var phone: String,
    var email: String,
    var password: String,
    var age: Int,
    var doctorUid: String = "0",
) {


}
package Models

import org.json.JSONObject

class Patient(
    uid: Long,
    firstName: String,
    lastName: String,
    address: String,
    phone: String,
    email: String,
    password: String,
    age: Int,
    doctorUid: Long,
    history: JSONObject,
    prescription: List<Medication>,
    results: JSONObject
) :
    User(
        uid, firstName, lastName,
        address,
        phone, email, password
    ) {


}
package Models

class Patient(uid: Long, firstName: String, lastName: String, address: String, phone: String) :
    User(
        uid, firstName, lastName,
        address,
        phone
    ) {


}
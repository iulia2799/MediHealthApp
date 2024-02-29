package Models

class Doctor(
    uid: Long, firstName: String, lastName: String, address: String, phone: String,
    email: String, password: String, patients: List<Patient>, appointments: List<Appointment>, officeHours: Schedule
) :
    User(uid, firstName, lastName, address, phone, email, password) {
}
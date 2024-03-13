package Models

data class Doctor(
    var uid: String,
    var firstName: String,
    var lastName: String,
    var address: String,
    var phone: String,
    var email: String,
    var password: String,
    var department: Department,
    var officeHours: Schedule = Schedule("9:00", "17:00", "Monday", "Friday")
) {
}
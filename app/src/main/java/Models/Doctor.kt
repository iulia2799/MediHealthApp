package Models

data class Doctor(
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var phone: String = "",
    var email: String = "",
    var password: String = "",
    var department: Department = Department.NA,
    var officeHours: Schedule = Schedule("9:00", "17:00", "Monday", "Friday"),
    var messageAvailable: Boolean = true,
    var available: Boolean = true,
)

val nullDoc = Doctor("", "", "", "", "", "", Department.NA)
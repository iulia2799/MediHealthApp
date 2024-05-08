package Models

import kotlinx.serialization.Serializable

data class Doctor(
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var phone: String = "",
    var email: String = "",
    var password: String = "",
    var department: Department = Department.NA,
    var officeHours: Schedule = Schedule("9:00", "17:00", "Monday", "Friday"),
    var messageAvailable: Boolean = true
)
@Serializable
data class DoctorSearchResult(
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var phone: String = "",
    var email: String = "",
    var department: Department = Department.NA,
    var officeHours: String ="",
    var messageAvailable: String = "",
    var objectID: String = ""
)

val nullDoc = Doctor("", "", "", "", "", "", Department.NA)
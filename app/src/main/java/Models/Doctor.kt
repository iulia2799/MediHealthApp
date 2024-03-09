package Models

data class Doctor(
    var uid: Long, var firstName: String, var lastName: String, var address: String, var phone: String,
    var email: String, var password: String, var patients: List<Patient>, var appointments: List<Appointment>, var officeHours: Schedule
)  {
}
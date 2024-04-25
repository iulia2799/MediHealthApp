package Models

data class Patient(
    var firstName: String = "",
    var lastName: String= "",
    var address: String= "",
    var phone: String= "",
    var email: String= "",
    var password: String= "",
    var age: Int = 0,
    var doctorUid: String = "0",
    var doctorName: String = ""
)

val nullPatient = Patient("","","","","","",0)
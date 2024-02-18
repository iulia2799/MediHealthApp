package Models

open class User(
    val uid: Long,
    val firstName: String,
    val lastName: String,
    val address: String,
    val phone: String,
    val email: String,
    val password: String
) {

}

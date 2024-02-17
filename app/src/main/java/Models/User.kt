package Models

open class User(
    val uid: Long,
    val firstName: String,
    val lastName: String,
    val address: String,
    val phone: String
) : IUser {
    override fun Login() {
        TODO("Not yet implemented")
    }

    override fun SignIn() {
        TODO("Not yet implemented")
    }

    override fun Logout() {
        TODO("Not yet implemented")
    }

    override fun ChangePassword() {
        TODO("Not yet implemented")
    }

}

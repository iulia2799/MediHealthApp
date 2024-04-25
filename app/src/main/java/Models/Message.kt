package Models

data class Message(
    var text: String = "",
    var sender: String = "",
    var receiver: String = "",
    var time: Long = System.currentTimeMillis()
)
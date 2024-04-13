package Models

data class Conversation(
    var user1Uid: String = "",
    var user2Uid: String = "",
    var user1Name: String = "first",
    var user2Name: String = "second",
    var messages: List<Message> = emptyList(),
    var lastUpdated: Long = System.currentTimeMillis()
) {
}
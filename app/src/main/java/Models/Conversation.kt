package Models

data class Conversation(
    var id: String,
    var user1Uid: String,
    var user2Uid: String,
    var messages: Array<String>
) {
}
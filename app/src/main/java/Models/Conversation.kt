package Models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObjects

data class Conversation(
    val userUids: List<String> = emptyList(),
    val userNames: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis(),
    var messagesRef: DocumentReference? = null
) {
    // Optional function to retrieve messages
    fun getMessages(callback: (List<Message>) -> Unit) {
        messagesRef?.collection("messages")?.get()?.addOnSuccessListener { snapshot ->
            val messages = snapshot.toObjects<Message>()
            callback(messages)
        }
    }
}
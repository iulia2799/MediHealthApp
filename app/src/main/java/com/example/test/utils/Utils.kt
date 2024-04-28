package com.example.test.utils

import Models.Conversation
import Models.DeviceToken
import Models.Message
import android.content.Context
import android.util.Log
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun sendTokenToServer(context: Context, firestore: FirebaseFirestore, token: String) {
    val ref = LocalStorage(context).getRef()
    val deviceToken = ref?.let { DeviceToken(it, token) }
    if (deviceToken != null) {
        firestore.collection(TOKEN_DATA).whereEqualTo("token", token).whereEqualTo("userUid", ref)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.size() == 0) {
                        firestore.collection(TOKEN_DATA).add(deviceToken)
                            .addOnCompleteListener {

                            }
                    }
                }
            }

    }
}

private val comparator = Comparator { message1: Message, message2: Message -> message1.time.compareTo(message2.time)}

fun getMessages(convesation: Conversation) = callbackFlow {

    val listener = convesation.messagesRef?.let {
        it.collection("messages").addSnapshotListener { data, error ->
            run {
                Log.d("hiiiiii", "hi")
                if (error != null) {
                    error.message?.let { it1 -> Log.d("ERRORRORORORORORO", it1) }
                    close(error)
                }
                Log.d("siiiiize", data?.size().toString())
                var messages = data?.toObjects<Message>()
                messages = messages?.sortedWith(comparator)
                trySend(messages)
            }
        }

    }
    awaitClose { listener?.remove() }
}

package com.example.test.utils

import Models.Conversation
import Models.DeviceToken
import Models.Message
import android.content.Context
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun sendTokenToServer(context: Context, firestore: FirebaseFirestore, token: String) {
    val ref = LocalStorage(context).getRef()
    val deviceToken = ref?.let { DeviceToken(it, token) }
    if (deviceToken != null) {
        firestore.collection(TOKEN_DATA).whereEqualTo("token", token).whereEqualTo("userUid",ref).get()
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

fun getMessages(convesation: Conversation) = callbackFlow {
    awaitClose {
        convesation.messagesRef?.path?.let {
            Firebase.firestore.collection(it).addSnapshotListener { data, error ->
                run {
                    if (error != null) {
                        close(error)
                    }
                    if (data != null) {
                        val messages = data.toObjects<Message>()

                        trySend(messages)
                    }
                }
            }
        }?.remove()
    }
}

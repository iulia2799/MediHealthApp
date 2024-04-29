package com.example.test.utils

import Models.Conversation
import Models.DeviceToken
import Models.Message
import android.content.Context
import android.util.Log
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun sendTokenToServer(context: Context, firestore: FirebaseFirestore, token: String) {
    val ref = LocalStorage(context).getRef()
    val deviceToken = ref?.let { DeviceToken(it, token) }
    if (deviceToken != null) {
        firestore.collection(TOKEN_DATA).whereEqualTo("token", token).whereEqualTo("userUid", ref)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.size() == 0) {
                        firestore.collection(TOKEN_DATA).add(deviceToken).addOnCompleteListener {

                        }
                    }
                }
            }

    }
}

private val comparator =
    Comparator { message1: Message, message2: Message -> message1.time.compareTo(message2.time) }

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

fun createNewConversation(
    context: Context,
    ref: String,
    name: String,
    callBack: (id: String) -> Unit
) {
    val localStorage = LocalStorage(context)
    val db = Firebase.firestore
    val userUids = emptyList<String>().toMutableList()
    val userName = emptyList<String>().toMutableList()
    if (localStorage.getRef()?.isNotEmpty() == true) {
        userUids += localStorage.getRef().toString()
        userUids += ref
        userName += localStorage.getName()
        userName += name
    }

    val conversation = Conversation(
        userUids, userName
    )
    db.collection(CONVO_LIST).add(conversation).addOnCompleteListener {
        if (it.isSuccessful) {
            callBack(it.result.id)
        } else {
            Log.d("ERROR", it.exception?.message.toString())
        }
    }
}

fun getNewConversation(id: String) = callbackFlow {

    val ref = Firebase.firestore.collection(CONVO_LIST).document(id)

    val listener = ref.let {
        it.addSnapshotListener { data, error ->
            run {
                if (error != null) {
                    error.message?.let { it1 -> Log.d("ERRORRORORORORORO", it1) }
                    close(error)
                }
                val conversation = data?.toObject<Conversation>()
                trySend(conversation)
            }
        }

    }
    awaitClose { listener.remove() }
}

fun sendFirstMessage(
    text: String,
    messageRef: DocumentReference,
    sender: String,
    receiver: String
) {
    val message = Message(
        text, sender, receiver
    )
    messageRef.collection("messages").add(message).addOnCompleteListener {
        if(!it.isSuccessful) {
            Log.d("error",it.exception?.message.toString())
        }
        Log.d("SUCCESS","SUCCESS")
    }
}

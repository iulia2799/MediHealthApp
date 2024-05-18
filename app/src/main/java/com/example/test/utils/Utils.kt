package com.example.test.utils

import Models.Billing
import Models.Conversation
import Models.DeviceToken
import Models.Message
import android.content.Context
import android.util.Log
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

fun removeTokenFromServer(ref: String, token: String) {
    val firestore = Firebase.firestore
    firestore.collection(TOKEN_DATA).whereEqualTo("token", token).whereEqualTo("userUid", ref)
        .get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.forEach { device ->
                    firestore.collection(TOKEN_DATA).document(device.id).delete().addOnCompleteListener {

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
    callBack: (String) -> Unit
) {
    val localStorage = LocalStorage(context)
    val db = Firebase.firestore
    val userUids = mutableListOf(localStorage.getRef().toString(), ref)
    val userUids2 = mutableListOf(ref, localStorage.getRef().toString())
    val userName = mutableListOf(localStorage.getName(), name)

    val conversation = Conversation(userUids, userName)


    val query = db.collection(CONVO_LIST).whereEqualTo("userUids",userUids)
    val permutted = db.collection(CONVO_LIST).whereEqualTo("userUids",userUids)

    query.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            if (task.result.isEmpty) {
                // No existing conversation, create a new one
                permutted.get().addOnCompleteListener { task2 ->
                    if(task2.isSuccessful) {
                        if(task2.result.isEmpty){
                            db.collection(CONVO_LIST).add(conversation).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    callBack(it.result.id) // New conversation created, pass null for error
                                } else {
                                    Log.d("fjdsfjorieghesupghrges",it.exception?.message.toString()) // Error creating conversation
                                }
                            }
                        } else {
                            val existingConversationId = task2.result.documents[0].id
                            callBack(existingConversationId) // Existing conversation found, pass null for error
                        }
                    }
                }
            } else {
                // Existing conversation found, return its ID
                val existingConversationId = task.result.documents[0].id
                callBack(existingConversationId) // Existing conversation found, pass null for error
            }
        } else {
            // Error fetching conversations
            Log.d("SFJREUOISGHE",task.exception?.message.toString())
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

fun sendBilling(billing: Billing, context: Context, onDone: () -> Unit) {
    val db = Firebase.firestore
    db.collection(BILLING_DATA).add(billing).addOnCompleteListener {
        if(it.isSuccessful) {
            onDone()
        } else {
            Log.d("Exception","Exception here: ${it.exception?.message.toString()}")
        }
    }
}

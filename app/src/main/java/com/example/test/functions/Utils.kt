package com.example.test.functions

import Models.DeviceToken
import android.content.Context
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.firestore.FirebaseFirestore

fun sendTokenToServer(context: Context, firestore: FirebaseFirestore, token: String) {
    val ref = LocalStorage(context).getRef()
    val deviceToken = ref?.let { DeviceToken(it,token) }
    if(deviceToken != null) {
        val response = firestore.collection("deviceTokens").add(deviceToken).addOnCompleteListener {

        }
    }
}
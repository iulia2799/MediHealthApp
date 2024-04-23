package com.example.test.utils

import Models.DeviceToken
import android.content.Context
import com.example.test.LocalStorage.LocalStorage
import com.google.firebase.firestore.FirebaseFirestore

fun sendTokenToServer(context: Context, firestore: FirebaseFirestore, token: String) {
    val ref = LocalStorage(context).getRef()
    val deviceToken = ref?.let { DeviceToken(it, token) }
    if (deviceToken != null) {
        firestore.collection("deviceTokens").whereEqualTo("token", token).whereEqualTo("userUid",ref).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.size() == 0) {
                        firestore.collection("deviceTokens").add(deviceToken)
                            .addOnCompleteListener {

                            }
                    }
                }
            }

    }
}
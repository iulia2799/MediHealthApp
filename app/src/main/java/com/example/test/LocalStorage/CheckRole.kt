package com.example.test.LocalStorage

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

fun CheckEmail(email: String, context: Context){
    val db = Firebase.firestore
    val queryD = db.collection("doctors").whereEqualTo("email",email)
    val queryP = db.collection("patients").whereEqualTo("email",email)
    val localStorage = LocalStorage(context)
    queryD.get().addOnCompleteListener{
        if (it.isSuccessful && it.result.documents.size > 0) {
            val reference = it.result.documents[0]
            localStorage.putUserDetails(reference.get("uid").toString(),true,reference.id)

        } else {
            queryP.get().addOnCompleteListener{
                if(it.isSuccessful && it.result.documents.size > 0){
                    val reference = it.result.documents[0]
                    localStorage.putUserDetails(reference.get("uid").toString(), false, reference.id)
                } else {
                    Log.e("oops","OOPs")
                }
            }
        }
    }
}
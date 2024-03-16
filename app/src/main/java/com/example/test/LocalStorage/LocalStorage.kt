package com.example.test.LocalStorage

import Models.Doctor
import Models.Patient
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class LocalStorage(context: Context) {
    private val DETAILS = "userDetails"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(DETAILS, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun putUserDetails(uid: String, specialist: Boolean, ref: String) {
        editor.putString("UID", uid)
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.apply()
    }

    fun putUserDetails(uid: String, specialist: Boolean) {
        editor.putString("UID", uid)
        editor.putBoolean("SP", specialist)
        editor.apply()
    }

    fun putRef(ref: String) {
        editor.putString("REF", ref)
        editor.apply()
    }

    fun getUserUid(): String? {
        return preferences.getString("UID", "")
    }

    fun getRole(): Boolean {
        return preferences.getBoolean("SP", false)
    }

    fun getRef(): String? {
        return preferences.getString("REF", "")
    }

    fun clearDetails() {
        editor.clear()
        editor.apply()
    }

    fun getData(): Any? {
        val db = Firebase.firestore
        var result: Any? = null
        if (this.getRole()) {
            val document = this.getRef()
                ?.let {
                    db.collection("doctors").document(it).get().addOnSuccessListener {
                        if(it.exists()) {
                            result = it.toObject<Doctor>()
                        }
                    }
                }
        } else {
            val document = this.getRef()
                ?.let {
                    db.collection("patients").document(it).get().addOnSuccessListener {
                        if(it.exists()) {
                            result = it.toObject<Patient>()
                        }
                    }
                }
        }


        return result
    }
}
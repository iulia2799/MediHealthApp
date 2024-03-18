package com.example.test.LocalStorage

import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
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

    fun putUserDetails(uid: String, specialist: Boolean, ref: String, gp: Int) {
        editor.putString("UID", uid)
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP",gp)
        editor.apply()
    }
    fun putUserDetails(uid: String, specialist: Boolean, ref: String, gp: Int, name: String) {
        editor.putString("UID", uid)
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP",gp)
        editor.putString("NAME",name)
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

    fun getDep(): Int {
        return preferences.getInt("DEP", 0)
    }
    fun getName(): String? {
        return preferences.getString("NAME", "")
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

    fun getPatient(): Patient {
        var result = nullPatient
        var db = Firebase.firestore
        val document = this.getRef()
            ?.let { it ->
                db.collection("patients").document(it).get().addOnCompleteListener{
                    if(it.isSuccessful) {
                        val value = it.result
                        if(value.exists()) {
                            result = value.toObject<Patient>()!!
                        }
                    }
                }
            }
        return result;
    }

}
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

    fun putUserDetails(specialist: Boolean, ref: String) {

        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.apply()
    }

    fun putUserDetails(specialist: Boolean, ref: String, gp: Int) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP",gp)
        editor.apply()
    }
    fun putUserDetails(specialist: Boolean, ref: String, gp: Int, firstName: String, lastName: String) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP",gp)
        editor.putString("first",firstName)
        editor.putString("last",lastName)
        editor.apply()
    }
    fun putUserDetails(specialist: Boolean, ref: String, firstName: String, lastName: String) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putString("first",firstName)
        editor.putString("last",lastName)
        editor.apply()
    }

    fun getName(): String{
        return "${preferences.getString("first","")}, ${preferences.getString("last","")}"
    }

    fun putUserDetails(specialist: Boolean) {
        editor.putBoolean("SP", specialist)
        editor.apply()
    }

    fun putRef(ref: String) {
        editor.putString("REF", ref)
        editor.apply()
    }
    fun getDep(): Int {
        return preferences.getInt("DEP", 0)
    }

    fun getRole(): Boolean {
        return preferences.getBoolean("SP", false)
    }

    fun getRef(): String? {
        return preferences.getString("REF", "")
    }

    fun loginUser(){
        editor.putBoolean("logged",true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean{
        return preferences.getBoolean("logged",false)
    }

    fun putName(first: String, last:String) {
        editor.putString("first",first)
        editor.putString("last",last)
        editor.apply()
    }

    fun LogOut(){
        editor.remove("logged")
        editor.apply()
    }

    fun clearDetails() {
        editor.clear()
        editor.apply()
    }

}
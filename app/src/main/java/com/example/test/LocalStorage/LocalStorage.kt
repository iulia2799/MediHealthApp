package com.example.test.LocalStorage

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(context: Context) {
    private val _details = "userDetails"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(_details, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun putUserDetails(specialist: Boolean, ref: String) {

        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.apply()
    }

    fun putUserDetails(specialist: Boolean, ref: String, gp: Int) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP", gp)
        editor.apply()
    }

    fun putUserDetails(
        specialist: Boolean, ref: String, gp: Int, firstName: String, lastName: String
    ) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putInt("DEP", gp)
        editor.putString("first", firstName)
        editor.putString("last", lastName)
        editor.apply()
    }

    fun putUserDetails(specialist: Boolean, ref: String, firstName: String, lastName: String) {
        editor.putBoolean("SP", specialist)
        editor.putString("REF", ref)
        editor.putString("first", firstName)
        editor.putString("last", lastName)
        editor.apply()
    }

    fun getName(): String {
        return "${preferences.getString("first", "")}, ${preferences.getString("last", "")}"
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

    fun loginUser() {
        editor.putBoolean("logged", true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean("logged", false)
    }

    fun putName(first: String, last: String) {
        editor.putString("first", first)
        editor.putString("last", last)
        editor.apply()
    }

    fun logOutUser() {
        editor.remove("logged")
        editor.apply()
    }

    fun putToken(token: String) {
        editor.putString("deviceToken", token)
        editor.apply()
    }

    fun getToken(): String {
        return preferences.getString("deviceToken", "") ?: ""
    }

    fun clearDetails() {
        editor.clear()
        editor.apply()
    }

}
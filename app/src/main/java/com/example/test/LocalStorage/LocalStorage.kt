package com.example.test.LocalStorage

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(context: Context) {
    private val DETAILS = "userDetails"
    private val preferences: SharedPreferences = context.getSharedPreferences(DETAILS,Context.MODE_PRIVATE)
    private val editor  = preferences.edit()

    fun putUserDetails(uid: String, specialist: Boolean, ref: String) {
        editor.putString("UID",uid)
        editor.putBoolean("SP",specialist)
        editor.putString("REF",ref)
        editor.apply()
    }

    fun putUserDetails(uid: String, specialist: Boolean) {
        editor.putString("UID",uid)
        editor.putBoolean("SP",specialist)
        editor.apply()
    }

    fun putRef(ref: String) {
        editor.putString("REF",ref)
        editor.apply()
    }

    fun getUserUid(): String? {
        return preferences.getString("UID", "")
    }
    fun getRole(): Boolean {
        return preferences.getBoolean("SP",false)
    }
    fun getRef(): String? {
        return preferences.getString("REF","")
    }
    fun clearDetails() {
        editor.clear()
        editor.apply()
    }
}
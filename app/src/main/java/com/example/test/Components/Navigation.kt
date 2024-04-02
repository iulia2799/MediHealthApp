package com.example.test.Components

import android.content.Context
import android.content.Intent
import com.example.test.Connect.LoginActivity
import com.example.test.Connect.RegisterActivity
import com.example.test.LocalStorage.LocalStorage
import com.example.test.MainActivity

fun Back(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
}

fun LoginPageEnter(context: Context) {
    context.startActivity(Intent(context, LoginActivity::class.java))
}

fun RegisterPageEnter(context: Context) {
    context.startActivity(Intent(context, RegisterActivity::class.java))
}

fun Logout(context: Context) {
    val localStorage = LocalStorage(context)
    localStorage.clearDetails()
    context.startActivity(Intent(context, MainActivity::class.java))
}
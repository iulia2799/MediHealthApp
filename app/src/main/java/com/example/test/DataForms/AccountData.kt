package com.example.test.DataForms

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class RegisterForm(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)

data class LoginForm(val email: String, val password: String)

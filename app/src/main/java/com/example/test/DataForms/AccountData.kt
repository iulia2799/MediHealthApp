package com.example.test.DataForms

data class RegisterForm(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)

data class LoginForm(val email: String, val password: String)
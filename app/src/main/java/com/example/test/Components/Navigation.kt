package com.example.test.Components

import android.content.Context
import android.content.Intent
import com.example.test.MainActivity

fun Back(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
}
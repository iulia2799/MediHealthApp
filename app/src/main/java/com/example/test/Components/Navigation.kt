package com.example.test.Components

import Models.Conversation
import android.content.Context
import android.content.Intent
import com.example.test.Connect.LoginActivity
import com.example.test.Connect.RegisterActivity
import com.example.test.LocalStorage.LocalStorage
import com.example.test.LocalStorage.ParcelableConvo
import com.example.test.MainActivity
import com.example.test.Misc.ListOfDoctors
import com.example.test.Misc.ListOfPatients
import com.example.test.messaging.ConversationSpace
import com.example.test.utils.removeTokenFromServer

fun back(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
}

fun loginPageEnter(context: Context) {
    context.startActivity(Intent(context, LoginActivity::class.java))
}

fun registerPageEnter(context: Context) {
    context.startActivity(Intent(context, RegisterActivity::class.java))
}

fun logout(context: Context) {
    val localStorage = LocalStorage(context)
    localStorage.getRef()?.let { removeTokenFromServer(it,localStorage.getToken()) }
    localStorage.clearDetails()
    context.startActivity(Intent(context, MainActivity::class.java))
}

fun goToList(context: Context) {
    val local = LocalStorage(context)
    if (local.getRole()) {
        context.startActivity(Intent(context, ListOfPatients::class.java))
    } else {
        context.startActivity(Intent(context, ListOfDoctors::class.java))
    }
}

fun goToConvo(context: Context, convo: Conversation) {
    val intent = Intent(context, ConversationSpace::class.java)
    val parcel = ParcelableConvo(
        convo.userUids, convo.userNames, convo.lastUpdated, convo.messagesRef?.path.toString()
    )
    intent.putExtra("data", parcel)
    context.startActivity(intent)
}
package com.example.test.Components

import Models.Conversation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.contentColorFor
import androidx.compose.ui.platform.UriHandler
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
    localStorage.getRef()?.let { removeTokenFromServer(it, localStorage.getToken()) }
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

fun goToConvo(context: Context, convo: Conversation, callBack: () -> Unit = {}) {
    val intent = Intent(context, ConversationSpace::class.java)
    val parcel = ParcelableConvo(
        convo.userUids, convo.userNames, convo.lastUpdated, convo.messagesRef?.path.toString()
    )
    intent.putExtra("data", parcel)
    context.startActivity(intent)
    callBack()
}

fun goToGoogleMaps(address: String, context: Context) {
    val parsedAddress = address.replace(" ", "+")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$parsedAddress"))
    context.startActivity(intent)
}

fun goToMail(mail: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No app selected or found", Toast.LENGTH_SHORT).show()
    }
}

fun dialNumber(number: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
    context.startActivity(intent)
}
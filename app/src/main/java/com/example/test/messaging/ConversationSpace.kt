package com.example.test.messaging

import Models.Conversation
import Models.Message
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultIconButton
import com.example.test.Components.Header
import com.example.test.Components.MessageTextField
import com.example.test.Components.ReceivedMessage
import com.example.test.Components.SentMessage
import com.example.test.LocalStorage.LocalStorage
import com.example.test.LocalStorage.ParcelableConvo
import com.example.test.Profile.DoctorDialog
import com.example.test.Profile.PatientDialog
import com.example.test.ui.theme.AppTheme
import com.example.test.utils.getMessages
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class ConversationSpace : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun Content() {
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        val userRef = localStorage.getRef()
        var listState = rememberLazyListState()
        val initList = emptyList<Message>()
        var text by remember {
            mutableStateOf("")
        }
        var buffer by remember {
            mutableStateOf(initList)
        }
        var messageList by remember {
            mutableStateOf(initList)
        }
        val convo = intent.getParcelableExtra("data", ParcelableConvo::class.java)
        val data = convo?.let {
            Conversation(
                it.userUids,
                convo.userNames,
                convo.lastUpdated,
                Firebase.firestore.document(convo.messagesRef)
            )
        }
        val flow = data?.let { getMessages(it) }
        val coroutine = rememberCoroutineScope()
        coroutine.launch {
            flow?.collect {
                if (it?.isNotEmpty() == true) {
                    buffer = it
                    listState.animateScrollToItem(it.size - 1)
                }

            }
        }
        LaunchedEffect(key1 = buffer) {
            messageList = buffer

        }
        var active by remember {
            mutableStateOf(false)
        }
        val otheruser = data?.userUids?.find { uid -> uid != userRef }
        val index = data?.userUids?.indexOf(otheruser)
        val username = index?.let { data.userNames[it] }
        Scaffold(topBar = {
            if (username != null) {
                Header(username = username) {
                    active = !active
                }
            }
        }, bottomBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MessageTextField(mods = Modifier
                    .padding(10.dp)
                    .weight(1f),
                    text = text,
                    labelValue = "Send a message",
                    onTextChange = { newValue ->
                        text = newValue
                    })
                DefaultIconButton(onClick = {
                    val input = Message(
                        text = text, sender = userRef!!, receiver = otheruser!!
                    )
                    data.messagesRef?.collection("messages")?.add(input)?.addOnCompleteListener {
                        text = ""
                    }
                    data.messagesRef?.update("lastUpdated", System.currentTimeMillis())
                        ?.addOnCompleteListener {

                        }
                }, imageVector = Icons.AutoMirrored.Rounded.Send, description = "Send message")
            }
        }) { innerPadding ->
            LazyColumn(
                state = listState, modifier = Modifier.padding(innerPadding)
            ) {
                items(messageList) { message ->
                    if (userRef == message.receiver) {
                        ReceivedMessage(message = message)
                    } else {
                        SentMessage(message = message)
                    }

                }
            }

            if (active && otheruser != null) {
                if (localStorage.getRole()) {
                    PatientDialog(patientRef = otheruser, type = true) {
                        active = false
                    }
                } else {
                    DoctorDialog(docRef = otheruser, type = false) {
                        active = false
                    }
                }
            }
        }
    }
}


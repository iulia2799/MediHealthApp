package com.example.test.messaging

import Models.Conversation
import Models.Message
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.LargeTextField
import com.example.test.Components.LongTextField
import com.example.test.Components.convertMillisToDate
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalPrimary
import com.example.test.utils.getMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConversationSpace : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Screen()
            }
        }
    }


    @Composable
    fun Screen() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Content()
        }
    }

    @Composable
    @Preview
    fun Content() {
        val initial = emptyList<Message>()
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        val reference = intent.getStringExtra("convo")
        val db = Firebase.firestore
        var messageList by remember {
            mutableStateOf(initial)
        }
        var username by remember {
            mutableStateOf("first")
        }
        var scrollState = rememberScrollState()
        if (reference != null) {
            val flow = getMessages(Conversation())
            LaunchedEffect(key1 = flow){
                flow.collectLatest {
                    messageList += it
                }
            }
            /*val docRef = db.collection("convos").document(reference)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val currentUser = localStorage.getRef()
                    Log.d("SUCCESS", "Current data: ${snapshot.data}")
                    username = if (currentUser !== snapshot.data?.get("user1Uid")) {
                        (snapshot.data?.get("user1Name") ?: "").toString()
                    } else {
                        (snapshot.data?.get("user2Name") ?: "").toString()
                    }

                } else {
                    Log.d("NO_CONTENT", "Current data: null")
                }
            }*/

        }
        Scaffold(topBar = { Header(username = username) }, bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LongTextField(text = "some text", labelValue = "Send a message")
            }
        }) { padValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padValue)
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val lastIndex = messageList.lastIndex
                        if(lastIndex >= 0) {
                            val coroutineScope = rememberCoroutineScope()
                            coroutineScope.launch {
                                scrollState.scrollTo(lastIndex)
                            }
                        }
                        LazyColumn {
                            items(messageList) {
                                if (it.receiver === username) {
                                    ReceivedMessage(message = it)
                                } else {
                                    SentMessage(message = it)
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    @Composable
    fun ReceivedMessage(message: Message) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(boldPrimary), contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = convertMillisToDate(message.time),
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontFamily = jejugothicFamily,
                        color = universalAccent
                    )
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(10.dp),
                        fontFamily = jejugothicFamily,
                        fontSize = 16.sp
                    )
                }

            }

        }
    }

    @Composable
    fun SentMessage(message: Message) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(universalPrimary), contentAlignment = Alignment.CenterEnd
            ) {
                Column {
                    Text(
                        text = convertMillisToDate(message.time),
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontFamily = jejugothicFamily,
                        color = universalAccent
                    )
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(10.dp),
                        fontFamily = jejugothicFamily,
                        fontSize = 16.sp
                    )
                }

            }

        }
    }

    @Composable
    fun Header(username: String) {
        Row {
            LargeTextField(
                value = "Conversation with $username",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
                    .weight(1f)
            )
        }
    }
}
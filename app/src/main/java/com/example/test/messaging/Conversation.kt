package com.example.test.messaging

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
import androidx.compose.foundation.layout.heightIn
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
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Conversation : ComponentActivity() {
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
    fun NewScaffold() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold() {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {

                }
            }
        }
    }

    @Composable
    fun Content() {
        val initial = emptyList<Message>()
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        val reference = intent.getStringExtra("convo")
        val db = Firebase.firestore
        var messageList by remember {
            mutableStateOf(initial)
        }
        if (reference != null) {
            val docRef = db.collection("medications").document(reference)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ERROR", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("SUCCESS", "Current data: ${snapshot.data}")
                } else {
                    Log.d("NO_CONTENT", "Current data: null")
                }
            }

        }
        val username = localStorage.getName()
        Column(modifier = Modifier.fillMaxSize()) {
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
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    ReceivedMessage()
                    SentMessage()

                }/*LazyColumn {
                items(messageList) {
                    if (it.receiver === username) {
                        ReceivedMessage(message = it)
                    } else {
                        SentMessage(message = it)
                    }
                }
            }*/
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LongTextField(text = "some text", labelValue = "Send a message")
            }
        }
    }

    @Composable
    @Preview
    fun ReceivedMessage() {
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
                        text = "Current time in millis",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontFamily = jejugothicFamily,
                        color = universalAccent
                    )
                    Text(
                        text = "hello",
                        modifier = Modifier.padding(10.dp),
                        fontFamily = jejugothicFamily,
                        fontSize = 16.sp
                    )
                }

            }

        }
    }

    @Composable
    @Preview
    fun SentMessage() {
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
                        text = "Current time in millis",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontFamily = jejugothicFamily,
                        color = universalAccent
                    )
                    Text(
                        text = "some message",
                        modifier = Modifier.padding(10.dp),
                        fontFamily = jejugothicFamily,
                        fontSize = 16.sp
                    )
                }

            }

        }
    }

    @Composable
    @Preview
    fun Header() {

    }
}
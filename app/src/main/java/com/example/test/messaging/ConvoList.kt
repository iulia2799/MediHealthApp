package com.example.test.messaging

import Models.Conversation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.convertMillisToDate
import com.example.test.Components.getFromUserUids
import com.example.test.Components.goToList
import com.example.test.LocalStorage.LocalStorage
import com.example.test.LocalStorage.ParcelableConvo
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.convoColor
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.utils.CONVO_LIST
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ConvoList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun Content() {
        val context = LocalContext.current
        val db = Firebase.firestore
        val localStorage = LocalStorage(context)
        val ref = localStorage.getRef()
        val initialList = emptyList<Conversation>()
        var list by remember {
            mutableStateOf(initialList)
        }
        if (ref != null) {
            db.collection(CONVO_LIST).whereArrayContains("userUids", ref)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        if (!value.isEmpty) {
                            val results = value.documents
                            list = emptyList()
                            results.iterator().forEach {
                                val element = it.toObject<Conversation>()
                                if (element != null) {
                                    Log.d("element", element.userNames[0])
                                    list += element
                                }
                            }
                        }

                    } else {
                        if (error != null) {
                            error.message?.let { Log.d("error", it) }
                        }
                    }
                    if (error != null) {
                        error.message?.let { Log.d("error2", it) }
                    }
                }
        }
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text(text = "Conversations") },
                    modifier = Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                    ),
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = appBarContainerColor,
                        titleContentColor = Color.Black,
                    ),
                    actions = {
                        FloatingActionButton(
                            onClick = {
                                goToList(context)
                            },
                            modifier = Modifier.padding(5.dp),
                            containerColor = universalBackground,
                            contentColor = universalPrimary
                        ) {
                            Icon(
                                Icons.Default.MailOutline,
                                contentDescription = "New Conversation",
                                tint = universalPrimary
                            )
                        }
                    },
                )
            }) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    items(list) { convo ->
                        ConvoItem(convo)
                    }
                }
            }
        }
    }

    @Composable
    fun ConvoItem(convo: Conversation) {
        val context = LocalContext.current
        val pair = getFromUserUids(convo, context)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(convoColor),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Row {
                    Text(text = pair.second, fontFamily = jejugothicFamily)
                }
                Row {
                    Text(
                        text = "Last updated: ${convertMillisToDate(convo.lastUpdated)}",
                        fontFamily = jejugothicFamily
                    )
                }
            }
            Column {
                IconButton(
                    onClick = { getToConversation(convo, context) }, modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = unfocusedLabelColor
                    )
                }
            }
        }
    }

    private fun getToConversation(conversation: Conversation, context: Context) {
        val parcel = conversation.messagesRef?.let {
            ParcelableConvo(
                conversation.userUids, conversation.userNames, conversation.lastUpdated, it.path
            )
        }
        val intent = Intent(context, ConversationSpace::class.java)
        intent.putExtra("data", parcel)
        context.startActivity(intent)
    }

}



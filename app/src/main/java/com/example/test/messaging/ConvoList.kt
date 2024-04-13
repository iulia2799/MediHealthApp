package com.example.test.messaging

import Models.Conversation
import Models.Message
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.test.Profile.Profile
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.convoColor
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary

class ConvoList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun Content() {
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
                                //todo
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
                    items(6) {
                        ConvoItem()
                    }
                }
            }
        }
    }

    @Composable
    @Preview
    fun ConvoItem() {
        val context = LocalContext.current
        //val initialConversation = Conversation()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(convoColor), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Row {
                    Text(text = "current", fontFamily = jejugothicFamily)
                }
                Row {
                    Text(text = "current", fontFamily = jejugothicFamily)
                }
            }
            Column {
                IconButton(
                    onClick = { /* todo */ },
                    modifier = Modifier.size(50.dp)
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

}



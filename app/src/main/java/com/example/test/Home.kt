package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.Welcome
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContent()
        }
    }
}

@Composable
@Preview
fun setContent() {
    AppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            HomeContent()
        }
    }
}

@Composable
fun HomeContent() {
    ScaffoldExample()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample() {
    var presses by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                ),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = appBarContainerColor,
                    titleContentColor = Color.Black,
                ),
                title = {
                    Welcome(name = "First Name")
                },
                actions = { FloatingActionButton(onClick = { presses++ }, modifier = Modifier.padding(5.dp), containerColor = universalBackground, contentColor = universalPrimary) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = universalPrimary)
                }},

            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }, contentColor = universalPrimary, containerColor = universalBackground) {
                Icon(Icons.Default.MailOutline, contentDescription = "Message")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text =
                """
                    This is an example of a scaffold. It uses the Scaffold composable's parameters to create a screen with a simple top app bar, bottom app bar, and floating action button.

                    It also contains some basic inner content, such as this text.

                    You have pressed the floating action button $presses times.
                """.trimIndent(),
            )
        }
    }
}
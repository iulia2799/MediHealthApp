package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.Greeting
import com.example.test.Connect.LoginActivity
import com.example.test.Connect.RegisterActivity
import com.example.test.Misc.HelpPage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = universalBackground
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content() {
    val context = LocalContext.current
    Column(modifier = Modifier
        .height(500.dp)
        .wrapContentWidth(Alignment.CenterHorizontally)) {
        Box(modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)) {
            Greeting()
        }
        CustomElevatedButton(
            { LoginPageEnter(context) },
            Alignment.TopCenter,
            "Login",
            Modifier
                .height(100.dp)
                .padding(20.dp)
                .fillMaxWidth()
        )
        CustomElevatedButton(
            { RegisterPageEnter(context) },
            Alignment.Center,
            "Register",
            Modifier
                .height(100.dp)
                .padding(20.dp)
                .fillMaxWidth()
        )
    }
    CustomElevatedButton(
        { HelpPageEnter(context) },
        Alignment.BottomCenter,
        "Help",
        Modifier.padding(100.dp)
    )
}

fun LoginPageEnter(context: Context) {
    context.startActivity(Intent(context, LoginActivity::class.java))
}

fun RegisterPageEnter(context: Context) {
    context.startActivity(Intent(context, RegisterActivity::class.java))
}

fun HelpPageEnter(context: Context) {
    context.startActivity(Intent(context, HelpPage::class.java))
}

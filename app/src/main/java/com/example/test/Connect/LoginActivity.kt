package com.example.test.Connect

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.CustomTextField
import com.example.test.Components.LargeTextField
import com.example.test.Connect.ui.theme.TestTheme
import com.example.test.ui.theme.universalBackground

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Screen()
            }
        }
    }
}

@Preview
@Composable
fun Screen() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = universalBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    LargeTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center), value = "Login to account"
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            ) {
                CustomTextField(labelValue = "Email")
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            ) {
                CustomTextField(labelValue = "Password")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row {
                CustomElevatedButton(
                    onClick = { GoToRegister(context) }, Alignment.Center,
                    "Login",
                    Modifier
                        .height(100.dp)
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }

            Row {
                CustomElevatedButton(
                    onClick = { Back(context) }, Alignment.Center,
                    "Back",
                    Modifier
                        .height(100.dp)
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

fun LoginToAccount(context: Context) {

}

fun GoToRegister(context: Context) {

}

fun Back(context: Context) {

}

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.Back
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.CustomTextField
import com.example.test.Components.LargeTextField
import com.example.test.Components.RegisterPageEnter
import com.example.test.Connect.ui.theme.TestTheme
import com.example.test.ui.theme.universalBackground
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                Screen()
            }
        }
    }
    @Preview
    @Composable
    fun Screen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
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
                    CustomTextField(
                        text = email,
                        labelValue = "Email",
                        onTextChange = { newValue -> email = newValue })
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                ) {
                    CustomTextField(text = password, labelValue = "Password", onTextChange = { newValue -> password = newValue}, type = "password")
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    CustomElevatedButton(
                        onClick = { LoginToAccount(context) }, Alignment.Center,
                        "Login",
                        Modifier
                            .height(100.dp)
                            .padding(20.dp)
                            .fillMaxWidth()
                    )
                }

                Row {
                    CustomElevatedButton(
                        onClick = { RegisterPageEnter(context) }, Alignment.Center,
                        "Create a new account",
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
        //auth.signInWithEmailAndPassword("iulia","iulia")
    }

}


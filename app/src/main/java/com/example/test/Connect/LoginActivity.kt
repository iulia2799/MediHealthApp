package com.example.test.Connect

import android.content.Intent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.Back
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.RegisterPageEnter
import com.example.test.Components.emailPattern
import com.example.test.Components.passwordPattern
import com.example.test.Home
import com.example.test.LocalStorage.CheckEmail
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Screen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun Screen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState){data ->
                    Snackbar(snackbarData = data, actionColor = universalAccent, containerColor = universalBackground, contentColor = universalError)
                } },
                containerColor = universalBackground
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                            pattern = emailPattern,
                            onTextChange = { newValue -> email = newValue },
                            )

                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        CustomTextField(
                            text = password,
                            labelValue = "Password",
                            onTextChange = { newValue -> password = newValue },
                            type = "password",
                            pattern = passwordPattern
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        DefaultButton(
                            onClick = {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnFailureListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Failure to authenticate, please verify that you typed the correct credentials and try again.",
                                                actionLabel = null,
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    .addOnSuccessListener {
                                        CheckEmail(context = context, email = email)
                                        context.startActivity(
                                            Intent(
                                                context,
                                                Home::class.java
                                            )
                                        )
                                    }
                            }, Alignment.Center,
                            "Login",
                            Modifier
                                .height(100.dp)
                                .padding(20.dp)
                                .fillMaxWidth()
                        )
                    }

                    Row {
                        DefaultButton(
                            onClick = { RegisterPageEnter(context) }, Alignment.Center,
                            "Create a new account",
                            Modifier
                                .height(100.dp)
                                .padding(20.dp)
                                .fillMaxWidth()
                        )
                    }

                    Row {
                        DefaultButton(
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
    }
}


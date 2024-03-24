package com.example.test.Profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.LargeTextField
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// get profile from reference
class Profile : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                setContent()
            }
        }
    }

    @Composable
    @Preview
    fun setContent() {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        val ref = localStorage.getRef()
        val type = localStorage.getRole()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = universalBackground
        ) {
            Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState){data ->
                Snackbar(snackbarData = data, actionColor = universalAccent, containerColor = universalBackground, contentColor = universalError)
            } }) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(it)) {
                    Row {
                        LargeTextField(value = "Profile", modifier = Modifier.padding(8.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                    }


                    //all of this in edit mode
                    //first name, last name, phone, address

                    //patient -> age

                    //button to confirm // display snackbar
                }
            }
        }
    }
}
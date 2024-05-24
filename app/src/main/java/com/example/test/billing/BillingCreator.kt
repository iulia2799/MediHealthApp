package com.example.test.billing

import Models.Billing
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.example.test.utils.sendBilling

class BillingCreator : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = universalBackground
                ) {

                }
            }
        }
    }
}

@Composable
@Preview
fun Creator() {
    val context = LocalContext.current
    var initialSum by remember {
        mutableStateOf(0f)
    }
    var discount by remember {
        mutableStateOf(0f)
    }
    var finalSum by remember {
        mutableStateOf(0f)
    }
    var description by remember {
        mutableStateOf("")
    }
    var fileList by remember {
        mutableStateOf(emptyList<String>())
    }

    Scaffold(topBar = {
        LargeTextField(
            value = "Create billing",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }, bottomBar = {
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) {
            DefaultButton(onClick = {
                sendBilling(Billing(), context) {
                    //display snackbar todo
                }
            }, alignment = Alignment.Center, text = "Send", modifier = Modifier.padding(10.dp))
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            //form
        }
    }

}
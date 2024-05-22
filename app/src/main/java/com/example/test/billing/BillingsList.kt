package com.example.test.billing

import Models.Billing
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.Downloader
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.SmallTextField
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError

class BillingsList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun preview() {
    val snackbarHostState = remember { SnackbarHostState() }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            Scaffold(topBar = {
                TopAppBar(
                    title = {
                        LargeTextField(
                            value = "Your Billings",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(10.dp)
                        )
                    }, colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = universalBackground,
                        titleContentColor = Color.Black,
                    )
                )

            }, snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        actionColor = universalAccent,
                        containerColor = universalBackground,
                        contentColor = universalError
                    )
                }
            }, containerColor = universalBackground) { innerPadding ->
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(5) {
                        BillingCard()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun BillingCard() {
    val emptyBilling = Billing(doctorName = "Some doctor name")
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardColors(
            containerColor = universalAccent,
            contentColor = Color.White,
            disabledContentColor = Color.DarkGray,
            disabledContainerColor = Color.White
        )
    ) {
        Column {
            Row {
                MediumTextField(modifier = Modifier.padding(10.dp), value = emptyBilling.doctorName)
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Initial sum to pay: " + emptyBilling.finalSum.toString())
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Covered by insurance: " + emptyBilling.discount.toString() + "%")
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Final sum to pay: " + emptyBilling.finalSum.toString())
            }
            Row {
                DefaultButton(onClick = {
                    val downloader = Downloader(context = context)
                    emptyBilling.files.forEach {
                        downloader.downloadFromFirebaseStorage(
                            it
                        )
                    }
                }, alignment = Alignment.Center, text = "Download bills", modifier = Modifier.fillMaxWidth().padding(10.dp))
            }

        }
    }
}
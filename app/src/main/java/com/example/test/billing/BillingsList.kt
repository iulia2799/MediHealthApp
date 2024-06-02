package com.example.test.billing

import Models.Billing
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.Downloader
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.SmallTextField
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.example.test.utils.BILLING_DATA
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class BillingsList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Content()
            }
        }
    }
}

@Composable
fun Content() {
    val context = LocalContext.current
    val localStorage = LocalStorage(context)
    val userReference = localStorage.getRef()
    val db = Firebase.firestore

    var areResultsLoading by remember {
        mutableStateOf(false)
    }
    var billingsList by remember {
        mutableStateOf(emptyList<Billing>())
    }
    val query = db.collection(BILLING_DATA).whereEqualTo("patientUid",userReference).orderBy("unix",Query.Direction.DESCENDING)
    LaunchedEffect(key1 = userReference) {
        areResultsLoading = true
        query.addSnapshotListener { value, error ->
            if(error != null) {
                areResultsLoading = false
                Log.d("Error retrieving bills: ", error.message.toString())
            }
            if(value != null) {
                areResultsLoading = false
                billingsList = value.toObjects(Billing::class.java)
            }
        }
    }

    preview(billingsList)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun preview(billingList: List<Billing>) {
    val context = LocalContext.current
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
                    ),
                    actions = {
                        DefaultButton(onClick = { exportAll(billingList,context) }, alignment = Alignment.Center, text = "Export all", modifier = Modifier.padding(10.dp))
                    }
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
                    items(billingList) {
                        BillingCard(it)
                    }
                }
            }
        }
    }
}

@Composable
fun BillingCard(billing: Billing) {
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
                MediumTextField(modifier = Modifier.padding(10.dp), value = billing.doctorName)
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Initial sum to pay: " + billing.finalSum.toString() + billing.currency)
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Covered by insurance: " + billing.discount.toString() + "%")
            }
            Row(modifier = Modifier.padding(10.dp)) {
                SmallTextField(value = "Final sum to pay: " + billing.finalSum.toString() + billing.currency)
            }
            Row {
                DefaultButton(onClick = {
                    val downloader = Downloader(context = context)
                    billing.files.forEach {
                        downloader.downloadFromFirebaseStorage(
                            it
                        )
                    }
                }, alignment = Alignment.Center, text = "Download bills", modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            }

        }
    }
}

fun exportAll(list: List<Billing>, context: Context) {
    val downloader = Downloader(context)
    list.forEach { bill ->
        bill.files.forEach { string ->
            downloader.downloadFromFirebaseStorage(string)
        }
    }
}
package com.example.test.billing

import Models.Billing
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.FilePicker
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.makeDiscountedNumber
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.example.test.utils.sendBilling
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

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
    val localStorage = LocalStorage(context)
    val userUid = localStorage.getRef()
    val username = localStorage.getName()
    val storage = Firebase.storage
    val db = Firebase.firestore
    var patientUid by remember {
        mutableStateOf("")
    }

    var patientName by remember {
        mutableStateOf("")
    }

    var initialSum by remember {
        mutableStateOf("0")
    }
    var discount by remember {
        mutableStateOf("0")
    }
    var finalSum by remember {
        mutableStateOf("0")
    }
    var description by remember {
        mutableStateOf("")
    }
    var fileList by remember {
        mutableStateOf(emptyList<String>())
    }
    var account by remember {
        mutableStateOf("")
    }
    var covered by remember {
        mutableStateOf(false)
    }
    var currency by remember {
        mutableStateOf("")
    }

    var snackbarHostState = SnackbarHostState()
    val uriHandler = LocalUriHandler.current
    var isLoading by remember { mutableStateOf(false) }
    var optionalFilesUrl by remember { mutableStateOf<List<String>>(emptyList()) }
    var filePicker = FilePicker()
    val multipleFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        filePicker.pickMultipleFiles(result, { isLoading = !isLoading }, { downloadUri ->
            val uri = downloadUri.toString()
            optionalFilesUrl += uri
            Log.d("BILLING FILES", downloadUri.toString())
        }, storage, db, "${userUid}_${username}")
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                actionColor = universalAccent,
                containerColor = universalBackground,
                contentColor = universalError
            )
        }
    },
        topBar = {
            LargeTextField(
                value = "Create billing",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }, bottomBar = {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                DefaultButton(onClick = {
                    val newBilling = Billing()
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
            Row {
                CustomTextField(text = initialSum, labelValue = "Initial sum", onTextChange = {newValue ->
                    initialSum = newValue
                    finalSum = makeDiscountedNumber(initialSum,discount)
                })
            }
            Row {
                CustomTextField(text = discount, labelValue = "Discount", onTextChange = {newValue ->
                    discount = newValue
                    finalSum = makeDiscountedNumber(initialSum,discount)
                })
            }
            Row {
                MediumTextField(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally), value = "Final sum : ${finalSum}${currency}")
            }
            Row {
                CustomTextField(text = currency, labelValue = "Currency", onTextChange = {newValue ->
                    currency = newValue
                    finalSum = makeDiscountedNumber(initialSum,discount)
                })
            }
            Row {
                CustomTextField(text = account, labelValue = "Account Number to send money", onTextChange = {newValue ->
                    account = newValue
                })
            }
            Row {
                DefaultButton(
                    onClick = {
                        multipleFileLauncher.launch(
                            Intent(Intent.ACTION_GET_CONTENT).setType(
                                "*/*"
                            ).putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                        )
                    },
                    alignment = Alignment.Center,
                    modifier = Modifier.padding(4.dp),
                    text = "Pick files"
                )
            }
            if (optionalFilesUrl.isNotEmpty()) {
                optionalFilesUrl.forEachIndexed { index, url ->
                    var annotation = ""
                    annotation = url
                    val hyperLink = buildAnnotatedString {
                        append("Uploaded file URL: ")
                        pushStringAnnotation(tag = "URL", annotation = annotation)
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue, fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("URL (${index+1})")
                        }
                        pop()
                    }
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        ClickableText(text = hyperLink, onClick = { offset ->
                            hyperLink.getStringAnnotations(
                                tag = "URL", start = offset, end = offset
                            ).firstOrNull()?.let {
                                Log.d("valid URL", it.item)
                                uriHandler.openUri(it.item)
                            }
                        })
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { optionalFilesUrl -= optionalFilesUrl[index]
                            Log.d("Siiiiize:",optionalFilesUrl.size.toString())}) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Delete file",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                }
            }
        }
    }

}
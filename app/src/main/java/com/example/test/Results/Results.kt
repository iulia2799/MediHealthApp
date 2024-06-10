package com.example.test.Results

import Models.ResultRecord
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.Downloader
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.convertMillisToExactDate
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.darkAccent
import com.example.test.ui.theme.offWhite
import com.example.test.utils.RESULTS_RECORDS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class Results : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @Composable
    @Preview
    fun Content() {
        db = Firebase.firestore
        storage = Firebase.storage
        val context = LocalContext.current
        val local = LocalStorage(context)
        var ref = local.getRef()
        var title = "Your results"
        val hasIntentExtra = intent.hasExtra("ref")
        if (hasIntentExtra) {
            ref = intent.getStringExtra("ref")
            title = "Patient results"
        }
        val query = when (hasIntentExtra) {
            true -> db.collection(RESULTS_RECORDS).whereEqualTo("patientRef", ref)
            false -> db.collection(RESULTS_RECORDS).whereEqualTo("patientRef", ref)
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        var results by remember {
            mutableStateOf(emptyList<ResultRecord>())
        }

        LaunchedEffect(key1 = ref) {
            isLoading = true
            query.orderBy("unix", Query.Direction.DESCENDING).get().addOnSuccessListener {
                results = it.toObjects()
                isLoading = false
            }.addOnFailureListener {
                Log.d("ERROR RETRIEVING", it.message.toString())
                isLoading = false
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                LargeTextField(
                    value = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (results.isEmpty()) {
                        LargeTextField(
                            value = "No results found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(10.dp)
                        )
                    } else {
                        DefaultButton(
                            onClick = {
                                val downloader = Downloader(context)
                                isLoading = true
                                results.forEach { element ->
                                    downloader.downloadFromFirebaseStorage(
                                        element.fileRefStorageUrl,
                                        "results_${System.currentTimeMillis()}"
                                    )
                                    if(element.optionalFiles.isNotEmpty()) {
                                        element.optionalFiles.forEach {
                                            downloader.downloadFromFirebaseStorage(
                                                it,
                                                "results_${System.currentTimeMillis()}"
                                            )
                                        }
                                    }
                                }
                                isLoading = false
                            },
                            alignment = Alignment.Center,
                            text = "Export results",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                    }
                    results.forEach { result ->
                        ResultCard(result)
                    }
                }

            }
        }
    }

    @Composable
    private fun ResultCard(result: ResultRecord) {
        val context = LocalContext.current
        val isDownloading by remember {
            mutableStateOf(false)
        }
        Card(
            modifier = Modifier
                .height(360.dp)
                .padding(16.dp),
            colors = CardColors(
                containerColor = offWhite,
                contentColor = darkAccent,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.Transparent
            )
        ) {
            Column {
                MediumTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = "Description: " + result.description
                )
                Spacer(modifier = Modifier.weight(1f))
                MediumTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = "Patient: " + result.patientName
                )
                Spacer(modifier = Modifier.weight(1f))
                MediumTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = "Doctor: " + result.doctorName
                )
                Spacer(modifier = Modifier.weight(1f))
                MediumTextField(modifier = Modifier.fillMaxWidth().padding(10.dp), value = convertMillisToExactDate(result.unix))
                Spacer(modifier = Modifier.weight(1f))
                DefaultButton(
                    onClick = {
                        val downloader = Downloader(context)
                        downloader.downloadFromFirebaseStorage(result.fileRefStorageUrl)
                    },
                    alignment = Alignment.Center,
                    text = "Download",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
                if (result.optionalFiles.isNotEmpty()) {
                    result.optionalFiles.forEach {
                        Spacer(modifier = Modifier.weight(1f))
                        DefaultButton(
                            onClick = {
                                val downloader = Downloader(context)
                                downloader.downloadFromFirebaseStorage(it)
                            },
                            alignment = Alignment.Center,
                            text = "Download optional files",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                if (isDownloading) {
                    CircularProgressIndicator()
                }

            }
        }
    }
}


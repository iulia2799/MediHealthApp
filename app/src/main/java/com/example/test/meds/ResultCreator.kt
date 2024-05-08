package com.example.test.meds

import Models.Patient
import Models.ResultRecord
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.LongTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.filterByFieldP
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalBackground
import com.example.test.utils.RECORDS_STORAGE
import com.example.test.utils.RESULTS_RECORDS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResultCreator : ComponentActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private val timeMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
        db = Firebase.firestore
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                SetContent()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun SetContent() {
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        val userUid = localStorage.getRef()
        val username = localStorage.getName()

        var patientRef by remember {
            mutableStateOf("")
        }
        var patientName by remember {
            mutableStateOf("")
        }
        var doctorRef by remember {
            mutableStateOf(userUid)
        }
        var doctorName by remember {
            mutableStateOf(username)
        }
        var description by remember {
            mutableStateOf("")
        }

        var data by remember { mutableStateOf(emptyMap<String, Patient>()) }

        var filteredData by remember { mutableStateOf(emptyMap<String, Patient>()) }

        db.collection("patients").get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.forEach { it1 ->
                    val app = it1.toObject<Patient>()
                    data += (it1.reference.id to app)
                }
                filteredData = data
            }
        }
        var text by remember { mutableStateOf("") }
        var active by remember {
            mutableStateOf(false)
        }

        var filePath by remember { mutableStateOf<Uri?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var uploadedFileUrl by remember { mutableStateOf<String?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.data != null) {
                isLoading = true
                val storageRef =
                    storage.reference.child("${patientRef}_${doctorRef}/${result.data!!.data?.lastPathSegment}_${timeMillis}")
                result.data!!.data?.let {
                    CoroutineScope(IO).launch {
                        try {
                            val snapshot = storageRef.putFile(it).await()
                            val downloadUri = snapshot.storage.downloadUrl.await()
                            uploadedFileUrl = downloadUri.toString()
                            isLoading = false
                            // Add the URL to Firestore (call separate function)
                            addUrlToFirestore(downloadUri.toString())
                        } catch (e: Exception) {
                            isLoading = false
                            // Handle upload failure with proper error messages
                            println("Error uploading file: $e")
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Row {
                    LargeTextField(
                        value = "Send Results", modifier = Modifier
                            .padding(10.dp)
                            .wrapContentSize(
                                Alignment.Center
                            )
                    )
                }
                Row {
                    DefaultButton(
                        onClick = {
                            launcher.launch(
                                Intent(Intent.ACTION_GET_CONTENT).setType(
                                    "*/*"
                                )
                            )
                        },
                        alignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp),
                        text = "Pick a file"
                    )
                    if (filePath != null) {
                        Text("File: ${filePath!!.path}")
                    }
                    if (uploadedFileUrl != null) {
                        Text("Uploaded File URL: $uploadedFileUrl")
                    }
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }

                Row {
                    SearchBar(query = text,
                        onQueryChange = { text = it },
                        onSearch = { filteredData = filterByFieldP(data, text) },
                        active = active,
                        onActiveChange = { active = it },
                        placeholder = {
                            Text(text = "Search for a patient")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                        },
                        trailingIcon = {
                            if (active) {
                                Icon(imageVector = Icons.Default.Clear,
                                    contentDescription = "close",
                                    modifier = Modifier.clickable {
                                        if (text.isNotEmpty()) {
                                            text = ""
                                        } else {
                                            active = false
                                        }
                                    })
                            }
                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                        ) {
                            filteredData.forEach {
                                val name = it.value.firstName + ", " + it.value.lastName
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                patientName = name
                                                patientRef = it.key
                                                active = false
                                            }, shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = name, style = TextStyle(
                                                    fontSize = 20.sp, fontFamily = jejugothicFamily
                                                )
                                            )
                                            Text(text = "Phone: ${it.value.phone}")
                                            Text(text = "Email: ${it.value.email}")
                                            Text(text = "Address: ${it.value.address}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    MediumTextField(modifier = Modifier, value = "Patient: $patientName")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    LongTextField(text = description, labelValue = "Description", onTextChange = {
                        description = it
                    })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    DefaultButton(
                        onClick = {
                            val res = doctorRef?.let {
                                uploadedFileUrl?.let { it1 ->
                                    ResultRecord(
                                        patientRef,
                                        patientName,
                                        it,
                                        doctorName,
                                        fileRefStorageUrl = it1,
                                        description
                                    )
                                }
                            }
                            if (res != null) {
                                uploadToFirestore(res, context)
                            }
                        },
                        alignment = Alignment.Center,
                        text = "Send",
                        modifier = Modifier.padding(4.dp)
                    )
                }

            }
        }
    }

    private fun uploadToFirestore(result: ResultRecord, context: Context) {
        db.collection(RESULTS_RECORDS).add(result).addOnSuccessListener {
            Toast.makeText(context, "Result sent", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {

        }
    }

    private fun addUrlToFirestore(fileUrl: String) {
        db.collection(RECORDS_STORAGE)
            .add(hashMapOf("fileUrl" to fileUrl)) // Add document with "fileUrl" field
            .addOnSuccessListener { documentReference ->
                println("Document written with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
}

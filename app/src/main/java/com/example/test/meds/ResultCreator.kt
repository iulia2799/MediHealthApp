package com.example.test.meds

import Models.Patient
import Models.ResultRecord
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.DefaultButton
import com.example.test.Components.FilePicker
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

        val uriHandler = LocalUriHandler.current

        val filePath by remember { mutableStateOf<Uri?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var uploadedFileUrl by remember { mutableStateOf<String?>(null) }
        var fileRefUrl by remember { mutableStateOf<String?>(null) }
        var optionalFilesUrl by remember { mutableStateOf<List<String>>(emptyList()) }

        val filePicker = FilePicker()

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            filePicker.pickFile(result, { isLoading = !isLoading }, { downloadUri ->
                uploadedFileUrl = downloadUri.toString()
                if (fileRefUrl == null) {
                    fileRefUrl = uploadedFileUrl
                }
            }, storage, db, "${patientRef}_${doctorRef}")/*if (result.data != null) {
                isLoading = true
                val storageRef =
                    storage.reference.child("${patientRef}_${doctorRef}/${result.data!!.data?.lastPathSegment}_${timeMillis}")
                result.data!!.data?.let {
                    CoroutineScope(IO).launch {
                        try {
                            val snapshot = storageRef.putFile(it).await()
                            val downloadUri = snapshot.storage.downloadUrl.await()
                            uploadedFileUrl = downloadUri.toString()
                            if(fileRefUrl == null) {
                                fileRefUrl = uploadedFileUrl
                            }
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
            }*/
        }

        val multipleFileLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            filePicker.pickMultipleFiles(result, { isLoading = !isLoading }, { downloadUri ->
                val uri = downloadUri.toString()
                optionalFilesUrl += uri
                Log.d("OPTIONAL FILES",downloadUri.toString())
            }, storage, db, "${patientRef}_${doctorRef}")
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
                        value = "Send Results",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
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
                    var annotation: String? = ""
                    if (filePath != null) annotation = filePath!!.path
                    else if (uploadedFileUrl != null) annotation = uploadedFileUrl
                    val hyperLink = buildAnnotatedString {
                        append("Uploaded file URL: ")
                        if (annotation != null) {
                            pushStringAnnotation(tag = "URL", annotation = annotation!!)
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Blue, fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Here is your valid URL")
                            }
                        }
                        pop()
                    }
                    if (annotation != null) {
                        ClickableText(text = hyperLink, onClick = { offset ->
                            hyperLink.getStringAnnotations(
                                tag = "URL", start = offset, end = offset
                            ).firstOrNull()?.let {
                                Log.d("valid URL", it.item)
                                uriHandler.openUri(it.item)
                            }
                        })
                    }
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
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
                        text = "Pick more files"
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
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
                    MediumTextField(
                        modifier = Modifier.padding(4.dp), value = "Patient: $patientName"
                    )
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
                                fileRefUrl?.let { it1 ->
                                    ResultRecord(
                                        patientRef = patientRef,
                                        patientName = patientName,
                                        doctorRef = it,
                                        doctorName = doctorName,
                                        fileRefStorageUrl = it1,
                                        description = description,
                                        optionalFiles = optionalFilesUrl
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

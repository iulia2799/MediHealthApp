package com.example.test.Profile

import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.LongTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.convertDateToTimeStamp
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

// get profile from reference
class Profile : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String
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
        val local = LocalStorage(context)

        var phone by remember {
            mutableStateOf("")
        }
        var address by remember {
            mutableStateOf("")
        }
        var age by remember {
            mutableStateOf("")
        }
        var doctorUid by remember {
            mutableStateOf("")
        }
        var datap by remember {
            mutableStateOf(nullPatient)
        }
        var datad by remember {
            mutableStateOf(nullDoc)
        } // Launch effect on composition
        ref = local.getRef().toString()
        type = local.getRole()
        if (!type) {
            this.ref.let { it ->
                db.collection("patients").document(it).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        if (value.exists()) {
                            val user = value.toObject<Patient>()!!
                            datap = user
                            phone = user.phone
                            address = user.address
                            age = user.age.toString()
                            doctorUid = user.doctorUid
                        }
                    }
                }
            }
        } else {
            this.ref.let { it ->
                db.collection("doctors").document(it).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        if (value.exists()) {
                            val user = value.toObject<Doctor>()!!
                            datad = user
                            phone = user.phone
                            address = user.address
                        }
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = universalBackground
        ) {
            Scaffold(snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        actionColor = universalAccent,
                        containerColor = universalBackground,
                        contentColor = universalError
                    )
                }
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(it)
                ) {
                    Row {
                        LargeTextField(
                            value = "Profile", modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    Row {
                        MediumTextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            value = if (type) datad.firstName else datap.firstName
                        )
                    }
                    Row {
                        MediumTextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            value = if (type) datad.lastName else datap.lastName
                        )
                    }
                    if (type) {
                        MediumTextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            value = datad.department.displayName
                        )
                    }
                    Row {
                        LongTextField(
                            text = phone,
                            labelValue = "Phone",
                            onTextChange = { newValue ->
                                phone = newValue
                            })
                    }
                    Row {
                        LongTextField(
                            text = address,
                            labelValue = "Address",
                            onTextChange = { newValue ->
                                address = newValue
                            })
                    }
                    if (datap.lastName.isNotEmpty()) {
                        Row {
                            LongTextField(
                                text = age,
                                labelValue = "Age",
                                onTextChange = { newValue ->
                                    age = newValue
                                })
                        }
                        //doctor uid search
                    }

                    Row {
                        DefaultButton(
                            onClick = {
                                if (datad.lastName.isNotEmpty()) {
                                    db.collection("doctors").document(ref).update(
                                        mapOf(
                                            "phone" to phone,
                                            "address" to address
                                        )
                                    ).addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Your profile was updated",
                                                actionLabel = null,
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } else {
                                    db.collection("patients").document(ref).update(
                                        mapOf(
                                            "phone" to phone,
                                            "address" to address,
                                            "age" to age.toInt(),
                                            "doctorUid" to doctorUid
                                        )
                                    ).addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Your profile was updated",
                                                actionLabel = null,
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            },
                            alignment = Alignment.Center,
                            text = "Change",
                            modifier = Modifier.padding(12.dp).fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
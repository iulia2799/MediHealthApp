package com.example.test.Misc

import Models.Conversation
import Models.Department
import Models.Patient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.MediumTextField
import com.example.test.Components.dialNumber
import com.example.test.Components.goToConvo
import com.example.test.Components.goToGoogleMaps
import com.example.test.Components.goToMail
import com.example.test.LocalStorage.LocalStorage
import com.example.test.Results.Results
import com.example.test.appointment.AppointmentManager
import com.example.test.meds.ListOfPrescriptions
import com.example.test.meds.MedicationManager
import com.example.test.services.Search
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.utils.PATIENTS
import com.example.test.utils.createNewConversation
import com.example.test.utils.getNewConversation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ListOfPatients : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Content()
            }
        }
    }

    @Composable
    fun Content() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            val db = Firebase.firestore
            var searchService = Search(LocalContext.current)
            var patientMap by remember { mutableStateOf(emptyMap<String, Patient>()) }
            var searchText by remember { mutableStateOf("") }
            val local = LocalStorage(LocalContext.current)
            val dep = local.getDep()
            val ref = local.getRef()
            var isLoading by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(searchText) {
                isLoading = true
                delay(1000)
                Log.d("COROUTINE", "SCOPE")
                var query = searchText
                if(dep == Department.GP.ordinal) query = "$searchText $ref"
                launch {
                    patientMap = searchService.retrievePatientValues(query)
                    isLoading = false
                }
                //val query = db.collection("doctors")
                /*query.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result!!.documents
                        val doctorMapTemp = mutableMapOf<String, Doctor>()
                        documents.forEach { document ->
                            val doctor = document.toObject<Doctor>()!!
                            doctorMapTemp[document.id] = doctor
                        }
                        doctorMap = doctorMapTemp
                    }
                }*/
            }

            /*LaunchedEffect(Unit) {
                var query = db.collection(PATIENTS).get()
                if (dep == Department.GP.ordinal) {
                    Log.d("gP", dep.toString())
                    query = db.collection(PATIENTS).whereEqualTo("doctorUid", ref).get()
                }
                query.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result!!.documents
                        val patientMapTemp = mutableMapOf<String, Patient>()
                        documents.forEach { document ->
                            val patient = document.toObject<Patient>()!!
                            patientMapTemp[document.id] = patient
                        }
                        patientMap = patientMapTemp
                    }
                }
            }*/


            Column {
                if (isLoading) MediumTextField(modifier = Modifier.fillMaxWidth(), value = "Loading...")
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for Patients") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(patientMap.keys.toList()) {
                        patientMap[it]?.let { it1 ->
                            PatientItem(
                                patient = it1, ref = it
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PatientItem(patient: Patient, ref: String) {
        val context = LocalContext.current
        val db = Firebase.firestore
        val localStorage = LocalStorage(context)
        val name = patient.firstName + ", " + patient.lastName
        val department = localStorage.getDep()
        val coroutine = rememberCoroutineScope()
        var convo by remember {
            mutableStateOf(Conversation())
        }
        var isLoading by remember {
            mutableStateOf(false)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), shape = RoundedCornerShape(8.dp),
            colors = CardColors(
                containerColor = universalPrimary,
                contentColor = Color.Black,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Black
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = name, style = TextStyle(fontSize = 20.sp, fontFamily = jejugothicFamily)
                )
                Text(text = "Phone: ${patient.phone}", color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        dialNumber(patient.phone, context)
                    })
                Text(text = "Email: ${patient.email}", color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        goToMail(patient.email, context)
                    })
                Text(text = "Address: ${patient.address}", color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        goToGoogleMaps(patient.address, context)
                    })
                Text(text = "Age: ${patient.age} years")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center)
                ) {
                    TextButton(
                        modifier = Modifier.padding(4.dp), onClick = {
                            val mode = "create"
                            val intent = Intent(context, AppointmentManager::class.java)
                            intent.putExtra("otherRef", ref)
                            intent.putExtra("otherName", name)
                            intent.putExtra("mode", mode)
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Make Appointment")
                    }
                    TextButton(
                        modifier = Modifier.padding(4.dp), onClick = {
                            val mode = "create"
                            val intent = Intent(context, MedicationManager::class.java)
                            intent.putExtra("otherRef", ref)
                            intent.putExtra("otherName", name)
                            intent.putExtra("mode", mode)
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Make Prescription")
                    }

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center)
                ) {
                    TextButton(
                        modifier = Modifier.padding(4.dp), onClick = {
                            val intent = Intent(context, ListOfPrescriptions::class.java)
                            intent.putExtra("ref", ref)
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Prescriptions")
                    }
                    // TODO: MAKE IT AVAILABLE FOR ALL DOCTORS
                    TextButton(
                        modifier = Modifier.padding(4.dp), onClick = {
                            val intent = Intent(context, Results::class.java)
                            intent.putExtra("ref", ref)
                            context.startActivity(intent)
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Results")
                    }


                    TextButton(
                        modifier = Modifier.padding(4.dp), onClick = {
                            createNewConversation(context, ref, name) { id ->
                                val flow = getNewConversation(id)
                                isLoading = true
                                coroutine.launch {
                                    flow.takeWhile { isLoading }.collect {
                                        if (it != null) {
                                            convo = it
                                            if (convo.messagesRef == null) {
                                                convo.messagesRef =
                                                    Firebase.firestore.document("convolist/$id")
                                            }
                                            goToConvo(context, convo) {
                                                isLoading = false
                                            }
                                        }

                                    }
                                }
                            }
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Message")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center)
                ) {
                    if (department > 1) {
                        TextButton(
                            modifier = Modifier.padding(4.dp), onClick = {
                                db.collection(PATIENTS).document(ref)
                                    .update(mapOf("doctorUid" to "")).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Patient successfully removed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Oops ! There was an error.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }, colors = ButtonDefaults.textButtonColors(
                                contentColor = universalAccent,
                            )
                        ) {
                            Text("Remove patient")
                        }
                    }
                }
            }
        }
    }
}

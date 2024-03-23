package com.example.test.Misc

import Models.Patient
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.appointment.AppointmentManager
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

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
            modifier = Modifier.fillMaxSize(),
            color = universalBackground
        ) {
            val db = Firebase.firestore
            var PatientMap by remember { mutableStateOf(emptyMap<String, Patient>()) }
            var searchText by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                val query = db.collection("patients")
                query.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val documents = it.result!!.documents
                        val PatientMapTemp = mutableMapOf<String, Patient>()
                        documents.forEach { document ->
                            val Patient = document.toObject<Patient>()!!
                            PatientMapTemp[document.id] = Patient
                        }
                        PatientMap = PatientMapTemp
                    }
                }
            }

            Column {
                // Search bar (optional)
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for Patients") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Display Patient list
                val filteredPatients = PatientMap.filter { it ->
                    searchText.isEmpty() ||
                            it.value.firstName.contains(searchText, ignoreCase = true) ||
                            it.value.lastName.contains(searchText, ignoreCase = true) ||
                            "${it.value.firstName} ${it.value.lastName}".contains(searchText, ignoreCase = true)
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredPatients.keys.toList()) { it ->
                        filteredPatients[it]?.let { it1 -> PatientItem(patient = it1, ref = it) } // Define a composable for each Patient item
                    }
                }
            }
        }
    }

    @Composable
    fun PatientItem(patient: Patient, ref: String) {
        val context = LocalContext.current
        val name = patient.firstName + ", " + patient.lastName
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = name,
                    style = TextStyle(fontSize = 20.sp, fontFamily = jejugothicFamily)
                )
                Text(text = "Phone: ${patient.phone}")
                Text(text = "Email: ${patient.email}")
                Text(text = "Address: ${patient.address}")
                Text(text = "Age: ${patient.age}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center)
                ) {
                    TextButton(
                        modifier = Modifier.padding(4.dp),
                        onClick = {
                            val mode = "create"
                            val intent = Intent(context, AppointmentManager::class.java)
                            intent.putExtra("otherRef", ref)
                            intent.putExtra("otherName", name)
                            intent.putExtra("mode", mode)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Make Appointment")
                        //to-do: medications
                    }
                }
            }
        }
    }
}

package com.example.test.Misc

import Models.Patient
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.test.ui.theme.AppTheme
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
                        filteredPatients[it]?.let { it1 -> PatientItem(Patient = it1, ref = it) } // Define a composable for each Patient item
                    }
                }
            }
        }
    }

    @Composable
    fun PatientItem(Patient: Patient, ref: String) {
        // Display Patient details (name, specialization, etc.)
        Text("Name: ${Patient.firstName}")
        // ... add more details or buttons as needed
    }
}

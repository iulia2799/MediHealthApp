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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

///SAME IMPLEMENTATION AS LIST OF patients, JUST DO NOT MAKE PRESCRIPTIONS AND APPLY DIFFERENT PATH
class ListOfPatients : ComponentActivity() {
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = universalBackground
        ) {
            val db = Firebase.firestore
            var patients by remember { mutableStateOf(emptyList<Patient>()) }
            var searchText by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                val query = db.collection("patients")
                query.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val retrievedpatients = it.result!!.toObjects(Patient::class.java)
                        patients = retrievedpatients
                    }
                }
            }

            Column {
                // Search bar (optional)
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for patients") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Display Patient list
                val filteredpatients = if (searchText.isEmpty()) {
                    patients
                } else {
                    patients.filter { it.firstName.contains(searchText, ignoreCase = true) || it.lastName.contains(searchText, ignoreCase = true)
                            || "${it.firstName} ${it.lastName}".contains(searchText, ignoreCase = true)} // Example filtering by name
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredpatients) { Patient ->
                        PatientItem(Patient = Patient) // Define a composable for each Patient item
                    }
                }
            }
        }
    }

    @Composable
    fun PatientItem(Patient: Patient) {
        // Display Patient details (name, specialization, etc.)
        Text("Name: ${Patient.firstName}, ${Patient.lastName}")
        // ... add more details or buttons as needed
    }
}
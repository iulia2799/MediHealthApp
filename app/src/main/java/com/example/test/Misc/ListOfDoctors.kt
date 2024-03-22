package com.example.test.Misc

import Models.Doctor
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListOfDoctors : ComponentActivity() {
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
            var doctors by remember { mutableStateOf(emptyList<Doctor>()) }
            var searchText by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                val query = db.collection("doctors")
                query.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val retrievedDoctors = it.result!!.toObjects(Doctor::class.java)
                        doctors = retrievedDoctors
                    }
                }
            }

            Column {
                // Search bar (optional)
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for doctors") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Display doctor list
                val filteredDoctors = if (searchText.isEmpty()) {
                    doctors
                } else {
                    doctors.filter { it.firstName.contains(searchText, ignoreCase = true) || it.lastName.contains(searchText, ignoreCase = true)
                            || "${it.firstName} ${it.lastName}".contains(searchText, ignoreCase = true)} // Example filtering by name
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredDoctors) { doctor ->
                        DoctorItem(doctor = doctor) // Define a composable for each doctor item
                    }
                }
            }
        }
    }

    @Composable
    fun DoctorItem(doctor: Doctor) {
        // Display doctor details (name, specialization, etc.)
        Text("Name: ${doctor.firstName}")
        Text("Specialization: ${doctor.department}")
        // ... add more details or buttons as needed
    }
}

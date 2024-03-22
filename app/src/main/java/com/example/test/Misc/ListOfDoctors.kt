package com.example.test.Misc

import Models.Department
import Models.Doctor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.example.test.Components.FormSelector
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ListOfDoctors : ComponentActivity() {
    private var departmentList = Department.values().map { it.displayName }
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
        val db = Firebase.firestore
        var doctorMap by remember { mutableStateOf(emptyMap<String, Doctor>()) }
        var searchText by remember { mutableStateOf("") }
        var selectedDepartment by remember { mutableStateOf(departmentList[0]) } // State for selected department

        LaunchedEffect(Unit) {
            val query = db.collection("doctors")
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val documents = it.result!!.documents
                    val doctorMapTemp = mutableMapOf<String, Doctor>()
                    documents.forEach { document ->
                        val doctor = document.toObject<Doctor>()!!
                        doctorMapTemp[document.id] = doctor
                    }
                    doctorMap = doctorMapTemp
                }
            }
        }

        Column {
            // Dropdown for department filtering
            FormSelector(options = departmentList, selectedOption = selectedDepartment, onOptionSelected = {
                selectedDepartment = it
            }, text = "Select a department", modifier = Modifier.fillMaxWidth())

            // Search bar (optional)
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search for doctors") },
                modifier = Modifier.fillMaxWidth()
            )

            // Display doctor list with filtering
            val filteredDoctors = doctorMap.filterValues { doctor ->
                (selectedDepartment == Department.NA.displayName || doctor.department == Department.values().find {
                    it.displayName == selectedDepartment
                }) &&
                        (searchText.isEmpty() ||
                                doctor.firstName.contains(searchText, ignoreCase = true) ||
                                doctor.lastName.contains(searchText, ignoreCase = true) ||
                                "${doctor.firstName} ${doctor.lastName}".contains(searchText, ignoreCase = true)
                                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDoctors.values.toList()) { doctor ->
                    DoctorItem(doctor = doctor) // Define a composable for each doctor item
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

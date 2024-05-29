package com.example.test.Misc

import Models.Conversation
import Models.Department
import Models.Doctor
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.FormSelector
import com.example.test.Components.MediumTextField
import com.example.test.Components.goToConvo
import com.example.test.appointment.AppointmentManager
import com.example.test.services.Search
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalPrimary
import com.example.test.utils.createNewConversation
import com.example.test.utils.getNewConversation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ListOfDoctors : ComponentActivity() {
    private var departmentList = Department.values().map { it.displayName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @Composable
    fun Content() {
        val context = LocalContext.current
        var searchService = Search(context)
        var isLoading by remember {
            mutableStateOf(false)
        }
        var doctorMap by remember { mutableStateOf(emptyMap<String, Doctor>()) }
        var searchText by remember { mutableStateOf("") }
        var selectedDepartment by remember { mutableStateOf(departmentList[0]) }
        LaunchedEffect(searchText, selectedDepartment) {
            isLoading = true
            delay(1000)
            Log.d("COROUTINE", "SCOPE")
            launch {
                doctorMap = searchService.retrieveValues(searchText)
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

        Column {
            // Dropdown for department filtering
            if (isLoading) MediumTextField(modifier = Modifier.fillMaxWidth(), value = "Loading...")
            FormSelector(
                options = departmentList, selectedOption = selectedDepartment, onOptionSelected = {
                    selectedDepartment = it
                    searchText =
                        if (selectedDepartment != Department.NA.displayName) Department.values()
                            .find { el ->
                                el.displayName == selectedDepartment
                            }?.name ?: "NA"
                        else ""
                }, text = "Select a department", modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search for doctors") },
                modifier = Modifier.fillMaxWidth()
            )
            val filteredDoctors = doctorMap
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDoctors.keys.toList()) {
                    filteredDoctors[it]?.let { it1 ->
                        DoctorItem(
                            doctor = it1, ref = it
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DoctorItem(doctor: Doctor, ref: String) {
        var isLoading by remember {
            mutableStateOf(false)
        }
        val context = LocalContext.current
        val name = doctor.firstName + ", " + doctor.lastName
        val coroutine = rememberCoroutineScope()
        var convo by remember {
            mutableStateOf(Conversation())
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(10.dp), shape = RoundedCornerShape(8.dp),
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
                Text(text = "${doctor.department}")
                Text(text = "Phone: ${doctor.phone}")
                Text(text = "Email: ${doctor.email}")
                Text(text = "Address: ${doctor.address}")
                Text(text = "Schedule: ${doctor.officeHours.start} - ${doctor.officeHours.end}; ${doctor.officeHours.weekStart} to ${doctor.officeHours.weekend}")
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
                    if (doctor.messageAvailable) {
                        TextButton(
                            modifier = Modifier.padding(4.dp), onClick = {
                                createNewConversation(context, ref, name) { id ->
                                    val flow = getNewConversation(id)
                                    isLoading = true
                                    coroutine.launch {
                                        flow.takeWhile {
                                            isLoading
                                        }.collect {
                                            if (it != null) {
                                                convo = it
                                                Log.d("CONVERSATIONS", "going to conversation")
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

                }
            }
        }
    }
}

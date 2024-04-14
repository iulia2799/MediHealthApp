package com.example.test.meds

import Models.Department
import Models.Medication
import Models.Patient
import Models.nullPatient
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.FormSelector
import com.example.test.Components.LargeTextField
import com.example.test.Components.LongTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.TimeUnitToString
import com.example.test.Components.convertDayStampToHourAndMinute
import com.example.test.Components.convertTimeToTimestamp
import com.example.test.LocalStorage.LocalStorage
import com.example.test.Profile.PatientCard
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalTime

class MedicationManager : ComponentActivity() {
    private var patientRef: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun Content() {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var frequency by remember { mutableStateOf("") }
        var pills by remember { mutableStateOf(0) }
        var days by remember { mutableStateOf(0) }
        var department by remember { mutableStateOf(Department.NA.displayName) }
        var alarms by remember {
            mutableStateOf<Set<Long>>(emptySet())
        }
        val db = Firebase.firestore
        val context = LocalContext.current
        val snackbarState = SnackbarHostState()
        val coroutineScope = rememberCoroutineScope()

        var patient by remember {
            mutableStateOf(nullPatient)
        }
        val localStorage = LocalStorage(context)
        patientRef = intent.getStringExtra("otherRef").toString()

        LaunchedEffect(key1 = patientRef) {
            db.collection("patients").document(patientRef).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result.toObject<Patient>()
                    if (document != null) {
                        patient = document
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(), color = universalBackground
        ) {
            Scaffold(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarState) { data ->
                        Snackbar(
                            snackbarData = data,
                            actionColor = universalAccent,
                            containerColor = universalBackground,
                            contentColor = universalError
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    Row {
                        LargeTextField(
                            value = "Create prescription",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        PatientCard(patient = patient)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        LongTextField(labelValue = "Medication name",
                            text = name,
                            onTextChange = { newValue ->
                                name = newValue
                                Log.d("NAME", name)
                            })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        LongTextField(labelValue = "Frequency",
                            text = frequency,
                            onTextChange = { newValue -> frequency = newValue })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        LongTextField(labelValue = "Description",
                            text = description,
                            onTextChange = { newValue -> description = newValue })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        LongTextField(labelValue = "Number of pills",
                            text = pills.toString(),
                            type = "number",
                            onTextChange = { newValue ->
                                pills = newValue.toInt()
                            })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        LongTextField(labelValue = "Number of days",
                            text = days.toString(),
                            type = "number",
                            onTextChange = { newValue ->
                                days = newValue.toInt()
                            })
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        FormSelector(
                            options = Department.values().map { it.displayName },
                            selectedOption = department,
                            onOptionSelected = {
                                department = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    val currentTime by remember {
                        mutableStateOf(LocalTime.now())
                    }
                    val state = remember {
                        TimePickerState(
                            is24Hour = true,
                            initialHour = currentTime.hour,
                            initialMinute = currentTime.minute,
                        )
                    }
                    DefaultButton(onClick = {

                        alarms += convertTimeToTimestamp(state.hour, state.minute)

                        Log.d("alarms", alarms.size.toString())
                    }, alignment = Alignment.CenterStart, text = "Add Alarm", modifier = Modifier)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Set Alarm Time:")
                    Spacer(modifier = Modifier.weight(1f))


                    Row {
                        TimePicker(
                            state = state,
                            modifier = Modifier.padding(16.dp),
                            colors = TimePickerDefaults.colors(
                                timeSelectorSelectedContainerColor = universalAccent,
                                timeSelectorUnselectedContainerColor = universalAccent,
                                timeSelectorSelectedContentColor = universalPrimary,
                                timeSelectorUnselectedContentColor = universalPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        Column {
                            if (alarms.isNotEmpty()) {
                                LargeTextField(modifier = Modifier, value = "Added Alarms:")
                                alarms.forEach { alarmTime ->
                                    val pair = convertDayStampToHourAndMinute(alarmTime)
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        LargeTextField(
                                            value = "${TimeUnitToString(pair.first)}:${
                                                TimeUnitToString(
                                                    pair.second
                                                )
                                            }", modifier = Modifier.padding(4.dp)
                                        )
                                        Log.d("aaaaalalala", alarmTime.toString())
                                        IconButton(
                                            onClick = { alarms -= alarmTime },
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "remove",
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }

                    DefaultButton(
                        onClick = {
                            val med = localStorage.getRef()?.let { it1 ->
                                Department.values().find { dep -> dep.displayName == department }
                                    ?.let { it2 ->
                                        Medication(
                                            doctorUid = it1,
                                            patientUid = patientRef,
                                            patientName = patient.firstName + ", " + patient.lastName,
                                            doctorName = localStorage.getName(),
                                            medicationName = name,
                                            frequency = frequency,
                                            pills = pills,
                                            days = days,
                                            medType = it2,
                                            description = description,
                                            alarms = alarms.toList()
                                        )
                                    }
                            }
                            if (med != null) {
                                db.collection("medication").add(med).addOnSuccessListener {

                                    coroutineScope.launch {
                                        snackbarState.showSnackbar(
                                            "Creation was a success.",
                                            actionLabel = null,
                                            true,
                                            SnackbarDuration.Short
                                        )
                                    }


                                }.addOnFailureListener {
                                    coroutineScope.launch {
                                        snackbarState.showSnackbar(
                                            "Creation was a failure.",
                                            actionLabel = null,
                                            true,
                                            SnackbarDuration.Short
                                        )

                                    }
                                }
                            }
                        },
                        alignment = Alignment.Center,
                        text = "Submit",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

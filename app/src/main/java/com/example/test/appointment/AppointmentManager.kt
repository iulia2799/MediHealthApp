package com.example.test.appointment

import Models.Appointment
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.convertMillisToDate
import com.example.test.Components.convertTimeToTimestamp
import com.example.test.LocalStorage.AppointmentParceled
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime

class AppointmentManager : ComponentActivity() {
    private lateinit var appointment: Appointment
    private lateinit var db: FirebaseFirestore
    private lateinit var localStorage: LocalStorage
    private var mode: String = "edit"

    //notification
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        val parcel = intent.getParcelableExtra("appointment", AppointmentParceled::class.java)
        mode = intent.getStringExtra("mode").toString()
        appointment = Appointment(null, "", "", "", "", Timestamp.now(), 0)

        if (parcel != null) {
            appointment =
                Appointment(
                    parcel.ref,
                    parcel.doctorUid,
                    parcel.doctorName,
                    parcel.patientUid,
                    parcel.patientName,
                    parcel.date,
                    parcel.alocatedTime
                )
        }
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun Content() {
        val context = LocalContext.current
        localStorage = LocalStorage(context)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = universalBackground
        ) {
            Column(
                modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row {
                    LargeTextField(
                        value = "Edit Appointment", modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize(
                                Alignment.Center
                            )
                    )
                }
                //temporary for preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    val doctor by remember { mutableStateOf("") }
                    val patient by remember { mutableStateOf("") }
                    CustomTextField(text = doctor, labelValue = "Search a doctor")
                    CustomTextField(text = patient, labelValue = "Search a patient")
                }
                //
                when (mode) {
                    "create" -> {
                        val doctor by remember { mutableStateOf("") }
                        val patient by remember { mutableStateOf("") }
                        CustomTextField(text = doctor, labelValue = "Search a doctor")
                        CustomTextField(text = patient, labelValue = "Search a patient")
                    }

                    "edit" -> {
                        //change date and time
                    }

                    "confirm" -> {
                        //accepted or deleted
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    var date by remember { mutableStateOf("Change the date") }
                    var showToggle by remember { mutableStateOf(false) }
                    Box(contentAlignment = Alignment.Center) {
                        DefaultButton(
                            onClick = { showToggle = true },
                            alignment = Alignment.Center,
                            text = date,
                            modifier = Modifier
                        )
                    }
                    if (showToggle) {
                        DatePickerCard(onSelection = { date = it },
                            onDismiss = { showToggle = false })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    val datetime = LocalDateTime.now()
                    var state = remember {
                        TimePickerState(
                            is24Hour = true,
                            initialHour = datetime.hour,
                            initialMinute = datetime.minute
                        )
                    }
                    var selectedOption = convertTimeToTimestamp(state.hour, state.minute)
                    TimeInput(
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    val datetime = LocalDateTime.now()
                    var state = remember {
                        TimePickerState(
                            is24Hour = true,
                            initialHour = datetime.hour,
                            initialMinute = datetime.minute
                        )
                    }
                    var selectedOption = convertTimeToTimestamp(state.hour, state.minute)
                    TimeInput(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {

                    DefaultButton(
                        onClick = {
                            if(mode === "edit") {

                            } else if (mode === "create") {

                            } else if(mode === "confirm") {

                            }
                        },
                        alignment = Alignment.CenterStart,
                        text = "Confirm",
                        modifier = Modifier.padding(4.dp)
                    )


                }
            }


        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerCard(onSelection: (String) -> Unit, onDismiss: () -> Unit) {
        val state = rememberDatePickerState(selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        })
        val selected = state.selectedDateMillis?.let {
            convertMillisToDate(it)
        } ?: ""
        DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
            DefaultButton(
                onClick = {
                    onSelection(selected)
                    onDismiss()
                },
                alignment = Alignment.Center,
                text = "Confirm",
                modifier = Modifier.padding(4.dp)
            )
        },
            dismissButton = {
                DefaultButton(
                    onClick = { onDismiss() },
                    alignment = Alignment.Center,
                    text = "Cancel",
                    modifier = Modifier.padding(4.dp)
                )
            }) {
            DatePicker(state = state)
        }

    }


}

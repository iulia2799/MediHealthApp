package com.example.test.appointment

import Models.Appointment
import Models.Doctor
import Models.Patient
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.convertDateToTimeStamp
import com.example.test.Components.convertMillisToDate
import com.example.test.Components.convertTimestampToDate
import com.example.test.Components.filterByField
import com.example.test.Components.filterByFieldP
import com.example.test.LocalStorage.AppointmentParceled
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime


class AppointmentManager : ComponentActivity() {
    private var appointment: Appointment = Appointment("", "", "", "", "", Timestamp.now())
    private lateinit var db: FirebaseFirestore
    private lateinit var localStorage: LocalStorage
    private var mode: String = "edit"
    private var type = false
    private var appRef: String = ""
    private var otherRef: String = ""
    private var otherName: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        val parcel = intent.getParcelableExtra("appointment", AppointmentParceled::class.java)
        mode = intent.getStringExtra("mode").toString()
        appointment = Appointment("", "", "", "", "", Timestamp.now(), 0)
        if (mode == "create") {
            otherRef = intent.getStringExtra("otherRef").toString()
            otherName = intent.getStringExtra("otherName").toString()
            if (type) {
                appointment.patientUid = otherRef
                appointment.patientName = otherName
            } else {
                appointment.doctorUid = otherRef
                appointment.doctorName = otherName
            }
        }
        appRef = intent.getStringExtra("reference").toString()
        if (parcel != null) {
            appointment =
                Appointment(
                    parcel.doctorUid,
                    parcel.doctorName,
                    parcel.patientUid,
                    parcel.patientName,
                    parcel.description,
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
    fun Content() {
        val context = LocalContext.current
        db = Firebase.firestore
        localStorage = LocalStorage(context)
        type = localStorage.getRole()
        Log.d("TYYYYYPE", localStorage.getName())
        val formDate = convertTimestampToDate(appointment.date).first
        var date by remember { mutableStateOf(if (mode == "edit") formDate else "Change the date") }
        var doctor by remember { mutableStateOf(if (type) localStorage.getName() else appointment.doctorName) }
        var doctorUid by remember { mutableStateOf(if (type) localStorage.getRef() else appointment.doctorUid) }
        var patientUid by remember { mutableStateOf(if (!type) localStorage.getRef() else appointment.patientUid) }
        var patient by remember { mutableStateOf(if (!type) localStorage.getName() else appointment.patientName) }
        var description by remember {
            mutableStateOf(appointment.description)
        }
        val datetime = LocalDateTime.now()
        var state = remember {
            TimePickerState(
                is24Hour = true,
                initialHour = if (mode === "edit") convertTimestampToDate(appointment.date).second else datetime.hour,
                initialMinute = if (mode === "edit") convertTimestampToDate(appointment.date).third else datetime.minute,
            )
        }
        var text by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }
        var datad by remember { mutableStateOf(emptyMap<String, Doctor>()) }
        var datap by remember { mutableStateOf(emptyMap<String, Patient>()) }
        var filter1 by remember { mutableStateOf(emptyMap<String, Doctor>()) }
        var filter2 by remember { mutableStateOf(emptyMap<String, Patient>()) }

        var alocatedTime by remember { mutableStateOf("${appointment.alocatedTime / 60}") }
        var unit by remember { mutableStateOf("Minutes") }

        if (!type) {
            db.collection("doctors").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.forEach { it1 ->
                        val app = it1.toObject<Doctor>()
                        datad += (it1.reference.id to app)
                    }
                    filter1 = datad
                }
            }
        } else {
            db.collection("patients").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.forEach { it1 ->
                        val app = it1.toObject<Patient>()
                        datap += (it1.reference.id to app)
                    }
                    filter2 = datap
                }
            }
        }

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
                Row {
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        query = text,
                        onQueryChange = {
                            text = it
                        },
                        onSearch = {
                            if (!type) {
                                filter1 = filterByField(datad, text)
                            } else {
                                filter2 = filterByFieldP(datap, text)
                            }
                        },
                        active = active,
                        onActiveChange = {
                            active = it
                        },
                        placeholder = {
                            Text(text = "Search")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                        },
                        trailingIcon = {
                            if (active) {
                                Icon(imageVector = Icons.Default.Clear,
                                    contentDescription = "close",
                                    modifier = Modifier.clickable {
                                        if (text.isNotEmpty()) {
                                            text = ""
                                        } else {
                                            active = false
                                        }
                                    })
                            }

                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                        ) {
                            if (!type) {
                                filter1.forEach {
                                    val name = it.value.firstName + ", " + it.value.lastName
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    doctor = name
                                                    doctorUid = it.key
                                                    active = false
                                                },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = name,
                                                    style = TextStyle(
                                                        fontSize = 20.sp,
                                                        fontFamily = jejugothicFamily
                                                    )
                                                )
                                                Text(text = "Department: ${it.value.department.displayName}")
                                                Text(text = "Phone: ${it.value.phone}")
                                                Text(text = "Email: ${it.value.email}")
                                                Text(text = "Address: ${it.value.address}")
                                                Text(text = "Schedule: ${it.value.officeHours.start} - ${it.value.officeHours.end}; ${it.value.officeHours.weekStart} to ${it.value.officeHours.weekend}")
                                            }
                                        }
                                    }

                                }
                            } else {
                                filter2.forEach {
                                    val name = it.value.firstName + ", " + it.value.lastName
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    patient = name
                                                    patientUid = it.key
                                                    active = false
                                                },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = name,
                                                    style = TextStyle(
                                                        fontSize = 20.sp,
                                                        fontFamily = jejugothicFamily
                                                    )
                                                )
                                                Text(text = "Phone: ${it.value.phone}")
                                                Text(text = "Email: ${it.value.email}")
                                                Text(text = "Address: ${it.value.address}")
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    MediumTextField(modifier = Modifier, value = "Doctor: $doctor")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    MediumTextField(modifier = Modifier, value = "Patient: $patient")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {

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
                    CustomTextField(text = description, labelValue = "Description", onTextChange = {
                        description = it
                    })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
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

                OutlinedTextField(
                    value = alocatedTime,
                    onValueChange = { newValue ->
                        alocatedTime = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Enter alocatedTime:") },
                    isError = alocatedTime.isNotEmpty() && alocatedTime.toIntOrNull() == null
                )

                Row {
                    RadioButton(
                        selected = unit == "Minutes",
                        onClick = { unit = "Minutes" },
                        colors = RadioButtonDefaults.colors(selectedColor = universalAccent)
                    )
                    Text("Minutes")
                    RadioButton(
                        selected = unit == "Hours",
                        onClick = { unit = "Hours" },
                        colors = RadioButtonDefaults.colors(selectedColor = universalAccent)
                    )
                    Text("Hours")
                }

                var showSnackbar by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    DefaultButton(
                        onClick = {
                            Log.d("fdsfds", appRef)
                            if (mode == "edit") {
                                LocalStorage(context).LogOut()
                                val updateData = mapOf(
                                    "doctorUid" to doctorUid,
                                    "patientUid" to patientUid,
                                    "patientName" to patient,
                                    "doctorName" to doctor,
                                    "description" to description,
                                    "date" to convertDateToTimeStamp(
                                        state.hour,
                                        state.minute,
                                        date
                                    ),
                                    "alocatedTime" to convertAlocatedTime(alocatedTime, unit),
                                )

                                db.collection("appointments").document(appRef).update(updateData)
                                    .addOnSuccessListener {
                                        showSnackbar = true
                                    }.addOnFailureListener {
                                        it.message?.let { it1 -> Log.e("fdsfds", it1) }
                                    }
                            } else if (mode == "create") {
                                db.collection("appointments").add(
                                    Appointment(
                                        doctorUid as String,
                                        doctor,
                                        patientUid as String,
                                        patient,
                                        description,
                                        convertDateToTimeStamp(state.hour, state.minute, date),
                                        convertAlocatedTime(alocatedTime, unit)
                                    )
                                ).addOnSuccessListener {
                                    showSnackbar = true
                                }
                            }
                        },
                        alignment = Alignment.CenterStart,
                        text = "Confirm",
                        modifier = Modifier.padding(4.dp)
                    )


                }
                if (showSnackbar) {
                    Row {
                        Text(text = "Appointment Confirmed!")
                    }
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

    @Composable
    @Preview
    fun preview() {
        var alocatedTime by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("Minutes") }
        Column {
            OutlinedTextField(
                value = alocatedTime,
                onValueChange = { newValue ->
                    alocatedTime = newValue.filter { it.isDigit() }
                },
                label = { Text("Enter alocatedTime:") },
                isError = alocatedTime.isNotEmpty() && alocatedTime.toIntOrNull() == null
            )
            Row {
                RadioButton(
                    selected = unit == "Minutes",
                    onClick = { unit = "Minutes" },
                    colors = RadioButtonDefaults.colors(selectedColor = universalAccent)
                )
                Text("Minutes")
                RadioButton(
                    selected = unit == "Hours",
                    onClick = { unit = "Hours" },
                    colors = RadioButtonDefaults.colors(selectedColor = universalAccent)
                )
                Text("Hours")
            }
        }

    }

    fun convertAlocatedTime(time: String, unit: String): Long {
        val convertedUnits = time.toLong()
        return if (unit == "Minutes") {
            convertedUnits * 60L
        } else {
            convertedUnits * 3600L
        }
    }

}

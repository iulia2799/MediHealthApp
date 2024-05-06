package com.example.test.Profile

import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.LongTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.filterByField
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.DOCTORS
import com.example.test.utils.PATIENTS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class Profile : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                setContent()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun setContent() {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val local = LocalStorage(context)

        var start by remember {
            mutableStateOf("")
        }
        var end by remember {
            mutableStateOf("")
        }
        var weekstart by remember {
            mutableStateOf("")
        }
        var weekEnd by remember {
            mutableStateOf("")
        }

        var phone by remember {
            mutableStateOf("")
        }
        var address by remember {
            mutableStateOf("")
        }
        var age by remember {
            mutableStateOf("")
        }
        var doctorUid by remember {
            mutableStateOf("")
        }
        var doctorName by remember {
            mutableStateOf("")
        }
        var datap by remember {
            mutableStateOf(nullPatient)
        }
        var datad by remember {
            mutableStateOf(nullDoc)
        }
        var doctors by remember {
            mutableStateOf(emptyMap<String, Doctor>())
        }
        var filteredDoctors by remember {
            mutableStateOf(emptyMap<String, Doctor>())
        }
        var text by remember {
            mutableStateOf("")
        }
        var active by remember {
            mutableStateOf(false)
        }

        var available by remember {
            mutableStateOf(false)
        }
        ref = local.getRef().toString()
        type = local.getRole()
        if (!type) {
            this.ref.let { it ->
                db.collection(PATIENTS).document(it).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        if (value.exists()) {
                            val user = value.toObject<Patient>()!!
                            datap = user
                            phone = user.phone
                            address = user.address
                            age = user.age.toString()
                            doctorUid = user.doctorUid
                            doctorName = user.doctorName
                        }
                    }
                }
                db.collection(DOCTORS).whereEqualTo("department", "GP").get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            it.result.forEach { it1 ->
                                val d = it1.toObject<Doctor>()
                                doctors += (it1.reference.id to d)
                            }
                        }
                    }
            }
        } else {
            this.ref.let { it ->
                db.collection(DOCTORS).document(it).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val value = it.result
                        if (value.exists()) {
                            val user = value.toObject<Doctor>()!!
                            datad = user
                            phone = user.phone
                            address = user.address
                            start = user.officeHours.start
                            end = user.officeHours.end
                            weekstart = user.officeHours.weekStart
                            weekEnd = user.officeHours.weekend
                            available = user.available
                        }
                    }
                }
                filteredDoctors = doctors
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            Scaffold(snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        actionColor = universalAccent,
                        containerColor = universalBackground,
                        contentColor = universalTertiary,
                    )
                }
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(paddingValues)
                ) {
                    Row {
                        LargeTextField(
                            value = "Profile",
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    Row {
                        MediumTextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            value = if (type) datad.firstName else datap.firstName
                        )
                    }
                    Row {
                        MediumTextField(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            value = if (type) datad.lastName else datap.lastName
                        )
                    }
                    if (type) {
                        Row {
                            MediumTextField(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally),
                                value = datad.department.displayName
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = available, // Set your desired initial state
                                onCheckedChange = { newValue -> available = newValue }
                            )
                            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between checkbox and text field
                            MediumTextField(modifier = Modifier, value = "Are you available for messaging?")
                        }
                    }
                    Row {
                        LongTextField(
                            text = phone,
                            labelValue = "Phone",
                            onTextChange = { newValue ->
                                phone = newValue
                            })
                    }
                    Row {
                        LongTextField(
                            text = address,
                            labelValue = "Address",
                            onTextChange = { newValue ->
                                address = newValue
                            })
                    }
                    if (datap.lastName.isNotEmpty()) {
                        Row {
                            LongTextField(
                                text = age,
                                labelValue = "Age",
                                onTextChange = { newValue ->
                                    age = newValue
                                })
                        }
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .wrapContentSize(
                                    Alignment.Center
                                )
                        ) {
                            SearchBar(modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center),
                                query = text,
                                onQueryChange = { newValue ->
                                    text = newValue
                                },
                                onSearch = {
                                    filteredDoctors = filterByField(doctors, text)
                                },
                                active = active,
                                onActiveChange = { newValue ->
                                    active = newValue
                                },
                                placeholder = {
                                    Text(text = "Search")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "search"
                                    )
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

                                }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(
                                            rememberScrollState()
                                        )
                                ) {
                                    filteredDoctors.forEach {
                                        val name = it.value.firstName + ", " + it.value.lastName
                                        DoctorItemWithAction(doctor = it.value) {
                                            doctorName = name
                                            doctorUid = it.key
                                            active = false
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
                            MediumTextField(
                                modifier = Modifier.padding(10.dp), value = "Doctor: $doctorName"
                            )
                        }
                    }
                    if (datad.lastName.isNotEmpty()) {
                        Row {
                            CustomTextField(text = start,
                                labelValue = "Starting hour",
                                onTextChange = { newValue ->
                                    start = newValue
                                })
                            Spacer(modifier = Modifier.weight(1f))
                            CustomTextField(
                                text = end,
                                labelValue = "Ending hour",
                                onTextChange = { newValue ->
                                    end = newValue
                                })
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Row {
                            CustomTextField(text = weekstart,
                                labelValue = "Start of working week",
                                onTextChange = { newValue ->
                                    weekstart = newValue
                                })
                            Spacer(modifier = Modifier.weight(1f))
                            CustomTextField(text = weekEnd,
                                labelValue = "End of working week",
                                onTextChange = { newValue ->
                                    weekEnd = newValue
                                })
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    Row {
                        DefaultButton(
                            onClick = {
                                if (datad.lastName.isNotEmpty()) {
                                    val schedule = mapOf(
                                        "start" to start,
                                        "end" to end,
                                        "weekStart" to weekstart,
                                        "weekend" to weekEnd,
                                    )
                                    db.collection(DOCTORS).document(ref).update(
                                        mapOf(
                                            "phone" to phone,
                                            "address" to address,
                                            "officeHours" to schedule,
                                            "available" to available
                                        )
                                    ).addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Your profile was updated",
                                                actionLabel = null,
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } else {
                                    db.collection(PATIENTS).document(ref).update(
                                        mapOf(
                                            "phone" to phone,
                                            "address" to address,
                                            "age" to age.toInt(),
                                            "doctorUid" to doctorUid,
                                            "doctorName" to doctorName
                                        )
                                    ).addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Your profile was updated",
                                                actionLabel = null,
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            },
                            alignment = Alignment.Center,
                            text = "Change",
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
package com.example.test

import Models.Appointment
import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CenteredBox
import com.example.test.Components.DefaultButton
import com.example.test.Components.RegisterPageEnter
import com.example.test.Components.Welcome
import com.example.test.Components.calendar.CustomCalendar
import com.example.test.Components.calendar.WeeklyDataSource
import com.example.test.Components.zonedDateTimeToTimestampFirebase
import com.example.test.LocalStorage.LocalStorage
import com.example.test.appointment.AppointmentDialog
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.util.*
import kotlin.properties.Delegates

class Home : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            setContent()
        }
    }

    @Composable
    @Preview
    fun setContent() {
        val context = LocalContext.current
        val local = LocalStorage(context)
        var datap by remember {
            mutableStateOf(nullPatient)
        }
        var datad by remember {
            mutableStateOf(nullDoc)
        }
        ref = local.getRef().toString()
        type = local.getRole()
        if (!type) {
            this.ref.let { it ->
                    db.collection("patients").document(it).get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val value = it.result
                            if (value.exists()) {
                                val user = value.toObject<Patient>()!!
                                datap = user
                            }
                        }
                    }
                }
        } else {
            this.ref.let { it ->
                    db.collection("doctors").document(it).get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val value = it.result
                            if (value.exists()) {
                                val user = value.toObject<Doctor>()!!
                                datad = user
                            }
                        }
                    }
                }
        }

        AppTheme {
            // A surface container using the 'background' color from the theme
            if (type) {
                HomeContent(datad.firstName)
            } else {
                HomeContent(datap.firstName)
            }
        }
    }

    @Composable
    fun HomeContent(firstName: String) {
        HomeScaffold(firstName)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScaffold(firstName: String) {
        val context = LocalContext.current
        val source = WeeklyDataSource()
        var sourceModel by remember { mutableStateOf(source.getData(lastSelectedDate = source.today)) }
        var data by remember { mutableStateOf(emptyList<Appointment>()) }

        LaunchedEffect(sourceModel) {
            var field = "patientUid"
            if(type) {
                field = "doctorUid"
            }
            val today = sourceModel.currentDate.date
            val start = today.atStartOfDay(ZoneId.of("UTC"))
            val end = today.plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1)
            db.collection("appointments").whereEqualTo(field, ref)
                .whereGreaterThanOrEqualTo("date", zonedDateTimeToTimestampFirebase(start))
                .whereLessThan("date", zonedDateTimeToTimestampFirebase(end)).get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        Log.d("fdsfds",it.result.documents.toString())
                        if(it.result.size() == 0) {
                            Log.d("fdsfds",":null")
                            data = emptyList()
                        } else {
                            data = it.result.toObjects(Appointment::class.java)
                        }

                        Log.d("fdsfds",":fdsfdsfds")
                        Log.d("TODAT","tODAfdsfdsfdsY $today")
                    }else {
                        Log.w("SNAPSHOT", "Error getting documents:", it.exception)
                    }
                }
        }
        Scaffold(topBar = {
            TopAppBar(
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                ),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = appBarContainerColor,
                    titleContentColor = Color.Black,
                ),
                title = {
                    Welcome(name = firstName)
                },
                actions = {
                    FloatingActionButton(
                        onClick = { /*CREATE INTENT*/ },
                        modifier = Modifier.padding(5.dp),
                        containerColor = universalBackground,
                        contentColor = universalPrimary
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = universalPrimary
                        )
                    }
                },

                )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TO DO CREATE INTENT*/ },
                contentColor = universalPrimary,
                containerColor = universalBackground
            ) {
                Icon(Icons.Default.MailOutline, contentDescription = "Message")
            }
        }) { padding ->
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row {
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "Prescriptions",
                        Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "History",
                        Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                }
                Row {
                    CenteredBox {

                        CustomCalendar(sourceModel, onPrevClickListener = { startDate ->
                            // refresh the CalendarUiModel with new data
                            // by get data with new Start Date (which is the startDate-1 from the visibleDates)
                            val finalStartDate = startDate.minusDays(1)
                            sourceModel = source.getData(
                                startDate = finalStartDate,
                                lastSelectedDate = sourceModel.currentDate.date
                            )
                        }, onNextClickListener = { endDate ->
                            // refresh the CalendarUiModel with new data
                            // by get data with new Start Date (which is the endDate+2 from the visibleDates)
                            val finalStartDate = endDate.plusDays(2)
                            sourceModel = source.getData(
                                startDate = finalStartDate,
                                lastSelectedDate = sourceModel.currentDate.date
                            )
                        }, onDateClickListener = { date ->
                            // refresh the CalendarUiModel with new data
                            // by changing only the `selectedDate` with the date selected by User
                            sourceModel =
                                sourceModel.copy(currentDate = date, week = sourceModel.week.map {
                                    it.copy(
                                        isSelected = it.date.isEqual(date.date)
                                    )
                                })
                        })
                    }

                }
                Row {

                    if (type) {
                        UpdateList(results = data)
                    }

                }
                Row {
                    DefaultButton(
                        onClick = { },
                        Alignment.Center,
                        "Doctors",
                        Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "Results",
                        Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun UpdateList(results: List<Appointment>) {
        CenteredBox {
            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp)
            ) {
                // Use items composable to iterate through results and display appointments
                items(items = results) {
                    // Your composable to display appointment data
                    Card(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        var isOpen by remember {
                            mutableStateOf(false)
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Doctor: ${it.doctorName}")
                            Text(text = "Patient: ${it.patientName}")
                            Text(text = "Description: ${it.description}")
                            Text(text = "Date: ${it.date.toDate()}")
                            TextButton(onClick = {
                                isOpen = true
                            }) {
                                Text("View")
                            }
                        }
                        if(isOpen) {
                            it.ref?.let { it1 -> AppointmentDialog(appointment = it, ref = it1) {
                                isOpen = false
                            }
                            }
                        }
                    }
                }
            }

        }
    }
}

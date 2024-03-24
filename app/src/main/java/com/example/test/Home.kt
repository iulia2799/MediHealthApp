package com.example.test

import Models.Appointment
import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
import android.content.Intent
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
import com.example.test.Misc.ListOfDoctors
import com.example.test.Misc.ListOfPatients
import com.example.test.appointment.AppointmentDialog
import com.example.test.meds.ResultCreator
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.time.ZoneId
import java.util.*
import kotlin.properties.Delegates

class Home : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContent()
        }
    }

    @Composable
    @Preview
    fun setContent() {
        db = Firebase.firestore
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
                local.putName(datad.firstName,datad.lastName)
                HomeContent(datad.firstName)
            } else {
                local.putName(datap.firstName,datap.lastName)
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
        var data by remember { mutableStateOf(emptyMap<String,Appointment>()) }

        LaunchedEffect(sourceModel) {
            var field = "patientUid"
            if(type) {
                field = "doctorUid"
            }
            Log.d("REF",ref)
            val today = sourceModel.currentDate.date
            val start = today.atStartOfDay(ZoneId.of("UTC"))
            val end = today.plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1)
            db.collection("appointments").whereEqualTo(field, ref)
                .whereGreaterThanOrEqualTo("date", zonedDateTimeToTimestampFirebase(start))
                .whereLessThan("date", zonedDateTimeToTimestampFirebase(end)).get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        Log.d("fdsfds",it.result.documents.toString())
                        if(it.result.size() == 0) {
                            Log.d("SIZE",":NOT EMPTY")
                            data = emptyMap()
                        } else {
                            it.result.forEach {it1 ->
                                val app = it1.toObject<Appointment>()
                                data += (it1.reference.id to app)
                            }

                        }

                        Log.d("SIZE",":EMPTY")
                        Log.d("TODAT","TODAY $today")
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

                        UpdateList(results = data)

                }
                Row {
                    if(!type) {
                        DefaultButton(
                            onClick = {
                                intent = Intent(context,ListOfDoctors::class.java)
                                context.startActivity(intent)
                            },
                            Alignment.Center,
                            "Doctors",
                            Modifier
                                .height(100.dp)
                                .width(200.dp)
                                .padding(20.dp)
                        )
                    } else {
                        DefaultButton(
                            onClick = {intent = Intent(context,ListOfPatients::class.java)
                                context.startActivity(intent) },
                            Alignment.Center,
                            "Patients",
                            Modifier
                                .height(100.dp)
                                .width(200.dp)
                                .padding(20.dp)
                        )
                    }

                    DefaultButton(
                        onClick = {
                            val intent = Intent(context, ResultCreator::class.java)
                            intent.putExtra("mode","create")
                            context.startActivity(intent)
                        },
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
    fun UpdateList(results: Map<String, Appointment>) {
        CenteredBox {
            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp)
            ) {
                // Use items composable to iterate through results and display appointments
                items(items = results.keys.toList()) {
                    // Your composable to display appointment data
                    Card(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        var isOpen by remember {
                            mutableStateOf(false)
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Doctor: ${results[it]?.doctorName}")
                            Text(text = "Patient: ${results[it]?.patientName}")
                            Text(text = "Description: ${results[it]?.description}")
                            Text(text = "Date: ${results[it]?.date?.toDate()}")
                            TextButton(onClick = {
                                isOpen = true
                            }) {
                                Text("View")
                            }
                        }
                        if(isOpen) {
                            results[it]?.let { it1 ->
                                AppointmentDialog(appointment = it1, ref = it, type = type) {
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

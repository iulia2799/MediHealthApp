package com.example.test

import Models.Appointment
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.test.LocalStorage.LocalStorage
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
        ref = local.getRef().toString()
        type = local.getRole()
        Log.d("User reference", "User reference: $ref")

        AppTheme {
            // A surface container using the 'background' color from the theme
            HomeContent()
        }
    }

    @Composable
    fun HomeContent() {
        HomeScaffold()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScaffold() {
        val context = LocalContext.current
        val source = WeeklyDataSource()
        var sourceModel by remember { mutableStateOf(source.getData(lastSelectedDate = source.today)) }
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
                    Welcome(name = "First Name")
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
                            Log.d("what","FDSFDSFSDFDSF")
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
                Row{
                    var results = listOf<Appointment>()
                    val today = source.today
                    val startOfDay = today.atStartOfDay(ZoneId.of("UTC"))
                    val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1)
                    if (type) {
                        val query = db.collection("appointments").whereEqualTo("doctorUid",ref)
                            .whereGreaterThanOrEqualTo("date",startOfDay).whereLessThan("date",endOfDay)
                        query.get().addOnCompleteListener{
                            if(it.isSuccessful) {
                                results = it.result.documents.map { doc ->
                                    doc.toObject<Appointment>()!!
                                }
                            }

                        }
                    }
                    CenteredBox {
                        LazyColumn{

                        }
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


}

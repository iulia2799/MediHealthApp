package com.example.test

import Models.Appointment
import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.test.Components.CenteredBox
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.Welcome
import com.example.test.Components.calendar.CustomCalendar
import com.example.test.Components.calendar.WeeklyDataSource
import com.example.test.Components.convertBooleanToResult
import com.example.test.Components.logout
import com.example.test.Components.zonedDateTimeToTimestampFirebase
import com.example.test.LocalStorage.LocalStorage
import com.example.test.Misc.ListOfDoctors
import com.example.test.Misc.ListOfPatients
import com.example.test.Profile.Profile
import com.example.test.Results.Results
import com.example.test.appointment.AppointmentDialog
import com.example.test.billing.BillingCreator
import com.example.test.billing.BillingsList
import com.example.test.meds.ListOfPrescriptions
import com.example.test.meds.ResultCreator
import com.example.test.messaging.ConvoList
import com.example.test.symptomchecker.CheckerActivity
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.darkPrimary
import com.example.test.ui.theme.offWhite
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.APPOINTMENTS_DATA
import com.example.test.utils.DOCTORS
import com.example.test.utils.PATIENTS
import com.example.test.utils.sendTokenToServer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.messaging.FirebaseMessaging
import java.time.ZoneId
import java.util.*
import kotlin.properties.Delegates

class Home : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var globalContext: Context
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAssssG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                LocalStorage(globalContext).putToken(token)
                sendTokenToServer(globalContext,db,token)
                Log.d("TAaaaaaaG1", token)
            })
        }
    }

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
        globalContext = context
        askNotificationPermission(context)
        val local = LocalStorage(context)
        onBackPressedDispatcher.addCallback {
            local.logOutUser()
            context.startActivity(Intent(context, MainActivity::class.java))
        }
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
                db.collection(PATIENTS).document(it).get().addOnCompleteListener {
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
                db.collection(DOCTORS).document(it).get().addOnCompleteListener {
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
            if (type) {
                local.putName(datad.firstName, datad.lastName)
                HomeContent(datad.firstName)
            } else {
                local.putName(datap.firstName, datap.lastName)
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
        var data by remember { mutableStateOf(emptyMap<String, Appointment>()) }
        var isLoading by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(sourceModel) {
            var field = "patientUid"
            if (type) {
                field = "doctorUid"
            }
            isLoading = true
            Log.d("REF", ref)
            val today = sourceModel.currentDate.date
            val start = today.atStartOfDay(ZoneId.of("UTC"))
            val end = today.plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1)
            db.collection(APPOINTMENTS_DATA).whereEqualTo(field, ref)
                .whereGreaterThanOrEqualTo("date", zonedDateTimeToTimestampFirebase(start))
                .whereLessThan("date", zonedDateTimeToTimestampFirebase(end))
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        data = emptyMap()
                        Log.d("fdsfds", value.documents.toString())
                        if (value.size() == 0) {
                            Log.d("SIZE", ":NOT EMPTY")
                            data = emptyMap()
                        } else {
                            value.forEach { it1 ->
                                val app = it1.toObject<Appointment>()
                                data += (it1.reference.id to app)
                            }

                        }

                        Log.d("SIZE", ":EMPTY")
                        Log.d("TODAT", "TODAY $today")
                        isLoading = false
                    } else {
                        isLoading = false
                        if (error != null) {
                            Log.w("SNAPSHOT", error.message.toString())
                        }
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
                        onClick = {
                            val intent = Intent(context, Profile::class.java)
                            context.startActivity(intent)
                        },
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
                onClick = {
                    context.startActivity(Intent(context, ConvoList::class.java))
                }, contentColor = universalPrimary, containerColor = universalBackground
            ) {
                Icon(Icons.Default.MailOutline, contentDescription = "Message")
            }
        }) { padding ->
            Column(
                modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row {
                    CenteredBox {

                        CustomCalendar(sourceModel, onPrevClickListener = { startDate ->
                            // refresh the ui model with new data
                            val finalStartDate = startDate.minusDays(1)
                            sourceModel = source.getData(
                                startDate = finalStartDate,
                                lastSelectedDate = sourceModel.currentDate.date
                            )
                        }, onNextClickListener = { endDate ->

                            val finalStartDate = endDate.plusDays(2)
                            sourceModel = source.getData(
                                startDate = finalStartDate,
                                lastSelectedDate = sourceModel.currentDate.date
                            )
                        }, onDateClickListener = { date ->
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
                    if (!type) {
                        DefaultButton(
                            onClick = {
                                intent = Intent(context, ListOfDoctors::class.java)
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
                            onClick = {
                                intent = Intent(context, ListOfPatients::class.java)
                                context.startActivity(intent)
                            },
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
                            if (type) {
                                val intent = Intent(context, ResultCreator::class.java)
                                intent.putExtra("mode", "create")
                                context.startActivity(intent)
                            } else {
                                val intent = Intent(context, Results::class.java)
                                context.startActivity(intent)
                            }

                        },
                        Alignment.Center,
                        "Results",
                        Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    DefaultButton(
                        onClick = {
                            if (type) {
                                context.startActivity(Intent(context, BillingCreator::class.java))
                            } else {
                                context.startActivity(Intent(context, BillingsList::class.java))
                            }
                        },
                        alignment = Alignment.Center,
                        text = "Billing",
                        modifier = Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(20.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    DefaultButton(
                        onClick = {
                            context.startActivity(Intent(context, CheckerActivity::class.java))
                        },
                        alignment = Alignment.Center,
                        text = "✨Virtual Assistant✨",
                        modifier = Modifier
                            .height(100.dp)
                            .width(300.dp)
                            .padding(20.dp),
                        fontWeight = FontWeight.Bold,
                        backgroundColor = darkPrimary,
                        contentColor = offWhite
                    )
                }
                Row {
                    if (!type) {
                        DefaultButton(
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        context, ListOfPrescriptions::class.java
                                    )
                                )
                            },
                            Alignment.Center,
                            "Prescriptions",
                            Modifier
                                .height(100.dp)
                                .width(200.dp)
                                .padding(20.dp)
                        )
                    }

                    DefaultButton(
                        onClick = {
                            logout(context)
                        },
                        Alignment.Center,
                        "Log out",
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
            if (results.keys.toList().isEmpty()) {
                LargeTextField(
                    value = "No Appointments",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = boldPrimary
                )
            }
            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp)
            ) {

                items(items = results.keys.toList()) {

                    val color =
                        if (results[it]?.accepted == true) universalBackground else universalTertiary
                    Card(
                        modifier = Modifier.padding(8.dp), colors = CardDefaults.cardColors(
                            containerColor = color
                        )
                    ) {
                        var isOpen by remember {
                            mutableStateOf(false)
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Doctor: ${results[it]?.doctorName}")
                            Text(text = "Patient: ${results[it]?.patientName}")
                            Text(text = "Description: ${results[it]?.description}")
                            Text(text = "Date: ${results[it]?.date?.toDate()}")
                            Text(text = "Accepted: ${
                                results[it]?.accepted?.let { it1 ->
                                    convertBooleanToResult(
                                        it1
                                    )
                                }
                            }")
                            TextButton(onClick = {
                                isOpen = true
                            }) {
                                Text("View")
                            }
                        }
                        if (isOpen) {
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


    private fun askNotificationPermission(context: Context) {
        Log.d("TAaaaaaaG", "token")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("HELLO", "TOJE3")
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAssssG", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result
                    Log.d("TAaaaaaaG2", token)
                    LocalStorage(context).putToken(token)
                    sendTokenToServer(context, db, token)
                })
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("NOTIFICATION", "OLD VERSION")
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG1", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("TAaaaaaaG3", token)
                LocalStorage(context).putToken(token)
                sendTokenToServer(context, db, token)
            })
        }
    }
}

package com.example.test

import Models.Appointment
import Models.Doctor
import Models.Patient
import Models.nullDoc
import Models.nullPatient
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.test.Components.CenteredBox
import com.example.test.Components.DefaultButton
import com.example.test.Components.Logout
import com.example.test.Components.Welcome
import com.example.test.Components.calendar.CustomCalendar
import com.example.test.Components.calendar.WeeklyDataSource
import com.example.test.Components.zonedDateTimeToTimestampFirebase
import com.example.test.LocalStorage.LocalStorage
import com.example.test.Misc.ListOfDoctors
import com.example.test.Misc.ListOfPatients
import com.example.test.Profile.Profile
import com.example.test.Results.Results
import com.example.test.appointment.AppointmentDialog
import com.example.test.meds.ListOfPrescriptions
import com.example.test.meds.ResultCreator
import com.example.test.messaging.ConvoList
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary
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
    private var type by Delegates.notNull<Boolean>()
    private lateinit var ref: String
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAssssG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d("TAaaaaaaG", token)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
        } else {
            // TODO: Inform user that that your app will not show notifications.
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
        askNotificationPermission()
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

        LaunchedEffect(sourceModel) {
            var field = "patientUid"
            if (type) {
                field = "doctorUid"
            }
            Log.d("REF", ref)
            val today = sourceModel.currentDate.date
            val start = today.atStartOfDay(ZoneId.of("UTC"))
            val end = today.plusDays(1).atStartOfDay(ZoneId.of("UTC")).minusNanos(1)
            db.collection("appointments").whereEqualTo(field, ref)
                .whereGreaterThanOrEqualTo("date", zonedDateTimeToTimestampFirebase(start))
                .whereLessThan("date", zonedDateTimeToTimestampFirebase(end)).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("fdsfds", it.result.documents.toString())
                        if (it.result.size() == 0) {
                            Log.d("SIZE", ":NOT EMPTY")
                            data = emptyMap()
                        } else {
                            it.result.forEach { it1 ->
                                val app = it1.toObject<Appointment>()
                                data += (it1.reference.id to app)
                            }

                        }

                        Log.d("SIZE", ":EMPTY")
                        Log.d("TODAT", "TODAY $today")
                    } else {
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
                modifier = Modifier.padding(padding),
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
                            Logout(context)
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
            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp)
            ) {

                items(items = results.keys.toList()) {

                    val color = when (results[it]?.accepted) {
                        true -> {
                            universalBackground
                        }

                        else -> {
                            universalTertiary
                        }
                    }
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
                            Text(text = "Accepted: ${results[it]?.accepted}")
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


    private fun askNotificationPermission() {
        Log.d("TAaaaaaaG", "token")
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("HELLO","TOJE3")
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAssssG", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast
                    //val msg = getString(R.string.msg_token_fmt, token)
                    Log.d("TAaaaaaaG", token)
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("HELLO","TOJE")
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAssssG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d("TAaaaaaaG", token)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
        }
    }
}

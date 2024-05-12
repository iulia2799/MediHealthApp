package com.example.test.meds

import Models.Medication
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CenteredBox
import com.example.test.Components.LargeTextField
import com.example.test.Components.convertDayStampToHourAndMinute
import com.example.test.LocalStorage.LocalStorage
import com.example.test.LocalStorage.PrescriptionParceled
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.MEDICATION_DATA
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ListOfPrescriptions : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @Composable
    @Preview
    fun Content() {
        val db = Firebase.firestore
        val context = LocalContext.current
        val localStorage = LocalStorage(context)
        var ref = localStorage.getRef()
        if (localStorage.getRole()) {
            val p = intent.getStringExtra("ref")
            ref = p
        }
        val snackbarState = SnackbarHostState()
        var prescriptions by remember { mutableStateOf(emptyMap<String, Medication>()) }
        var coroutine = rememberCoroutineScope()
        LaunchedEffect(key1 = ref) {
            db.collection(MEDICATION_DATA).whereEqualTo("patientUid", ref).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.size() > 0) {
                            it.result.forEach { it1 ->
                                val obj = it1.toObject<Medication>()
                                prescriptions += (it1.reference.id to obj)
                            }
                        }
                    }
                }
        }
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarState) { data ->
                        Snackbar(
                            snackbarData = data,
                            actionColor = universalAccent,
                            containerColor = universalBackground,
                            contentColor = universalTertiary
                        )
                    }
                }
            ) {paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Row {
                        var text = "Your prescriptions"
                        if (localStorage.getRole()) {
                            text = "Patient prescriptions"
                        }
                        LargeTextField(
                            value = text,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    if (prescriptions.isEmpty()) {
                        Row {
                            LargeTextField(
                                value = "No results",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    } else {
                        ListOfMedicationsCards(prescriptions) {
                            coroutine.launch {
                                snackbarState.showSnackbar(
                                    "Prescription was successfully deleted.",
                                    "ok",
                                    true,
                                    SnackbarDuration.Short
                                )
                            }

                        }
                    }
                }
            }


        }
    }
}

@Composable
fun ListOfMedicationsCards(list: Map<String, Medication>, removeCallBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val localStorage = LocalStorage(context)
    val intent = Intent(context, ChangeAlerts::class.java)
    CenteredBox {
        LazyColumn {
            items(list.keys.toList()) { index ->
                Card(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: ${list[index]?.medicationName}")
                        Text(text = "Doctor: ${list[index]?.doctorName}")
                        Text(text = "Patient: ${list[index]?.patientName}")
                        Text(text = "Description: ${list[index]?.description}")
                        Text(text = "Frequency: ${list[index]?.frequency}")
                        Text(text = "Pills: ${list[index]?.pills} pills")
                        Text(text = "Pills/portion: ${list[index]?.pillsPerPortion} pills")
                        Text(text = "Days: ${list[index]?.days} days")
                        Text(text = "Alarms:")
                        list[index]?.alarms?.forEach { alarm ->
                            val pair = convertDayStampToHourAndMinute(alarm)
                            Text(text = "${pair.first}:${pair.second}")
                        }
                        if (!localStorage.getRole()) {
                            TextButton(onClick = {
                                val parcel = list[index]?.let {
                                    PrescriptionParceled(
                                        it.doctorUid,
                                        it.patientUid,
                                        it.patientName,
                                        it.doctorName,
                                        it.frequency,
                                        it.medicationName,
                                        it.description,
                                        it.pills,
                                        it.days,
                                        it.medType,
                                        it.alarms
                                    )
                                }
                                intent.putExtra("item", parcel)
                                intent.putExtra("ref", index)
                                context.startActivity(intent)
                            }) {
                                Text("Change")
                            }
                        } else if(localStorage.getRef() == list[index]?.doctorUid) {
                            TextButton(onClick = {
                                db.collection(MEDICATION_DATA).document(index).delete().addOnCompleteListener {
                                    removeCallBack()
                                }
                            }) {
                                Text("Remove prescription")
                            }
                        }

                    }
                }
            }
        }
    }
}
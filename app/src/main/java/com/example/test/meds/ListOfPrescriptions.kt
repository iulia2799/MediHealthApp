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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.test.ui.theme.universalBackground
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ListOfPrescriptions : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
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
        val ref = localStorage.getRef()
        var prescriptions by remember { mutableStateOf(emptyMap<String, Medication>()) }
        LaunchedEffect(key1 = ref) {
            db.collection("medication").whereEqualTo("patientUid", ref).get()
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
            Column {
                Row {
                    LargeTextField(value = "Your prescriptions",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if(prescriptions.isEmpty()){
                    Row{
                        LargeTextField(value = "No results", modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally))
                    }
                } else {
                    ListOfMedicationsCards(prescriptions)
                }  
            }
            
        }
    }
}

@Composable
fun ListOfMedicationsCards(list: Map<String, Medication>) {
    val context = LocalContext.current
    val intent = Intent(context,ChangeAlerts::class.java)
    CenteredBox {
        LazyColumn{
            items(list.keys.toList()){index ->
                Card(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Doctor: ${list[index]?.medicationName}")
                        Text(text = "Patient: ${list[index]?.doctorName}")
                        Text(text = "Description: ${list[index]?.description}")
                        Text(text = "Frequency: ${list[index]?.frequency}")
                        Text(text = "Pills: ${list[index]?.pills} pills")
                        Text(text = "Days: ${list[index]?.days} days")
                        Text(text = "Alarms:")
                        list[index]?.alarms?.forEach { alarm ->
                            val pair = convertDayStampToHourAndMinute(alarm)
                            Text(text = "${pair.first}:${pair.second}")
                        }
                        TextButton(onClick = {
                            var parcel = list[index]?.let {
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
                            intent.putExtra("item",parcel)
                            intent.putExtra("ref",index)
                            context.startActivity(intent)
                        }) {
                            Text("Change")
                        }
                    }
                }
            }
        }
    }
}
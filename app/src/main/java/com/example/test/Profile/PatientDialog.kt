package com.example.test.Profile


import Models.Patient
import Models.nullPatient
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.dialNumber
import com.example.test.Components.goToGoogleMaps
import com.example.test.Components.goToMail
import com.example.test.appointment.AppointmentManager
import com.example.test.meds.MedicationManager
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.PATIENTS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

@Composable
fun PatientDialog(patientRef: String, type: Boolean = false, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val intent1 = Intent(context, AppointmentManager::class.java)
    val intent2 = Intent(context, MedicationManager::class.java)
    var patient by remember {
        mutableStateOf(nullPatient)
    }
    LaunchedEffect(key1 = patientRef) {
        db.collection(PATIENTS).document(patientRef).get().addOnSuccessListener {
            patient = it.toObject<Patient>()!!
        }.addOnFailureListener {
            Toast.makeText(context, "Oops there was an error.", Toast.LENGTH_SHORT).show()
        }
    }
    Dialog(
        onDismissRequest = { onDismiss() }, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp, universalTertiary
            ),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize(Alignment.Center)
            ) {
                LargeTextField(
                    value = "${patient.firstName} ${patient.lastName}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                MediumTextField(
                    value = "Address: ${patient.address}",
                    color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        goToGoogleMaps(patient.address, context)
                    })
                MediumTextField(
                    value = "Email: ${patient.email}",
                    color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        goToMail(patient.email, context)
                    })
                MediumTextField(
                    value = "Phone: ${patient.phone}",
                    color = boldPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        dialNumber(patient.phone, context)
                    })
                Text(text = "Age: ${patient.age}")
                if (type) {
                    TextButton(
                        onClick = {
                            intent1.putExtra(
                                "otherName", "${patient.firstName}, ${patient.lastName}"
                            )
                            intent1.putExtra("otherRef", patientRef)
                            intent1.putExtra("mode", "create")
                            context.startActivity(intent1)
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Schedule Appointment")
                    }
                    TextButton(
                        onClick = {
                            intent2.putExtra("otherRef", patientRef)
                            context.startActivity(intent2)
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Create Prescription")
                    }
                }
                TextButton(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(), onClick = {
                        onDismiss()
                    }, colors = ButtonDefaults.textButtonColors(
                        contentColor = universalAccent
                    )
                ) {
                    Text("Close")
                }

            }
        }
    }

}

@Composable
fun PatientCard(patient: Patient) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp, universalTertiary
        ),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            LargeTextField(
                value = "${patient.firstName} ${patient.lastName}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            MediumTextField(
                value = "Address: ${patient.address}",
                color = boldPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    goToGoogleMaps(patient.address, context)
                })
            MediumTextField(
                value = "Email: ${patient.email}",
                color = boldPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    goToMail(patient.email, context)
                })
            MediumTextField(
                value = "Phone: ${patient.phone}",
                color = boldPrimary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    dialNumber(patient.phone, context)
                })
            MediumTextField(value = "Age: ${patient.age} years", modifier = Modifier)

        }
    }
}

@Composable
fun PatientItemWithAction(patient: Patient, onClick: () -> Unit = {}) {
    val name = patient.firstName + ", " + patient.lastName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }, shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = name, style = TextStyle(
                        fontSize = 20.sp, fontFamily = jejugothicFamily
                    )
                )
                Text(text = "Phone: ${patient.phone}")
                Text(text = "Email: ${patient.email}")
                Text(text = "Address: ${patient.address}")
            }
        }
    }
}
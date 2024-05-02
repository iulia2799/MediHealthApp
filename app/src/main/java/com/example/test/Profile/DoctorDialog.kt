package com.example.test.Profile

import Models.Doctor
import Models.nullDoc
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.appointment.AppointmentManager
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.DOCTORS
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

@Composable
fun DoctorDialog(docRef: String, type: Boolean = false, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val intent = Intent(context, AppointmentManager::class.java)
    var doctor by remember {
        mutableStateOf(nullDoc)
    }
    LaunchedEffect(key1 = docRef) {
        db.collection(DOCTORS).document(docRef).get().addOnSuccessListener {
            doctor = it.toObject<Doctor>()!!
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
                .height(420.dp)
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
                    value = "${doctor.firstName} ${doctor.lastName} - ${doctor.department.displayName}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                MediumTextField(value = "Address: ${doctor.address}", modifier = Modifier)
                MediumTextField(value = "Email: ${doctor.email}", modifier = Modifier)
                MediumTextField(value = "Phone: ${doctor.phone}", modifier = Modifier)
                Text(text = "Schedule: ${doctor.officeHours.start} - ${doctor.officeHours.end}; ${doctor.officeHours.weekStart} to ${doctor.officeHours.weekend}")
                if (!type) {
                    TextButton(
                        onClick = {
                            intent.putExtra("otherName", "${doctor.firstName}, ${doctor.lastName}")
                            intent.putExtra("otherRef", docRef)
                            intent.putExtra("mode", "create")
                            context.startActivity(intent)
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Schedule Appointment")
                    }
                }

            }
        }
    }

}

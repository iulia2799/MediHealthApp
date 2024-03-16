package com.example.test.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalTertiary

@Composable
@Preview
fun AppointmentDialog(/*appointment: Appointment, onDismiss: () -> Unit*/) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp, universalTertiary
            ),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(
                containerColor = universalBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .wrapContentSize(Alignment.Center)
            ) {
                LargeTextField(value = "Appointment Details", modifier = Modifier.padding(4.dp))
                Spacer(modifier = Modifier.weight(1f))
                MediumTextField(modifier = Modifier.padding(10.dp), value = "Some value here")
                Spacer(modifier = Modifier.weight(1f))
                //MediumTextField(modifier = Modifier, value = "Doctor: ${appointment.doctorName}")
                //MediumTextField(modifier = Modifier, value = "Patient: ${appointment.patientName}")
                //MediumTextField(modifier = Modifier, value = "Date: ${appointment.date.toDate()}")
                //MediumTextField(modifier = Modifier, value = "Time: ${appointment.alocatedTime / 60} minutes")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentSize(Alignment.Center)
                ) {
                    TextButton(
                        modifier = Modifier.padding(4.dp),
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent,
                        )
                    ) {
                        Text("Edit")
                    }
                    TextButton(
                        modifier = Modifier.padding(4.dp),
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = universalAccent
                        )
                    ) {
                        Text("Delete")
                    }
                }

            }
        }
    }
}
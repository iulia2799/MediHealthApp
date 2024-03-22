package com.example.test.LocalStorage

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppointmentParceled(
    var doctorUid: String,
    var doctorName: String,
    var patientUid: String,
    var patientName: String,
    val description: String,
    var date: Timestamp,
    var alocatedTime: Long,
) : Parcelable {

}

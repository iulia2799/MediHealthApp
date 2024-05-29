package com.example.test.LocalStorage

import Models.Department
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
) : Parcelable

@Parcelize
data class PrescriptionParceled(
    val doctorUid: String = "0",
    val patientUid: String = "0",
    val patientName: String = "",
    val doctorName: String = "",
    val frequency: String = "",
    val medicationName: String = "",
    val description: String = "",
    val pills: Int = 0,
    val days: Int = 0,
    val medType: Department = Department.NA,
    val alarms: List<Long> = emptyList()
) : Parcelable

@Parcelize
data class ParcelableConvo(
    val userUids: List<String> = emptyList(),
    val userNames: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val messagesRef: String = ""
) : Parcelable


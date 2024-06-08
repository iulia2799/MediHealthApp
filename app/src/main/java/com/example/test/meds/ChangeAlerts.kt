package com.example.test.meds

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.convertTimeStampToHourAndMinute
import com.example.test.Components.convertTimeToTimestamp
import com.example.test.Components.convertToUtcDailySeconds
import com.example.test.Components.deconvertToDailySeconds
import com.example.test.LocalStorage.PrescriptionParceled
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.MEDICATION_DATA
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChangeAlerts : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    @Preview
    fun Content() {
        var myList = emptyList<Long>()
        val db = Firebase.firestore
        val snackbarState = SnackbarHostState()
        val coroutineScope = rememberCoroutineScope()
        val parcelable = intent.getParcelableExtra("item", PrescriptionParceled::class.java)
        val reference = intent.getStringExtra("ref")
        if (parcelable != null) {
            myList = parcelable.alarms
        }
        var list by remember {
            mutableStateOf(myList)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(), color = universalBackground
        ) {
            Scaffold(
                modifier = Modifier,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarState) { data ->
                        Snackbar(
                            snackbarData = data,
                            actionColor = universalAccent,
                            containerColor = universalBackground,
                            contentColor = universalTertiary
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = 1200.dp)
                ) {
                    Row {
                        LargeTextField(
                            value = "Change alerts",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    if (parcelable != null) {
                        Row {
                            MediumTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.Start),
                                value = "Name: ${parcelable.medicationName}"
                            )
                        }
                        Row {
                            MediumTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.Start),
                                value = "Description: ${parcelable.description}"
                            )
                        }
                        Row {
                            MediumTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.Start),
                                value = "Frequency: ${parcelable.frequency}"
                            )
                        }
                        Row {
                            MediumTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.Start),
                                value = "Doctor: ${parcelable.doctorName}"
                            )
                        }
                    }
                    Row {
                        DefaultButton(
                            onClick = {
                                if (reference != null) {
                                    db.collection(MEDICATION_DATA).document(reference).update(
                                        mapOf(
                                            "alarms" to list.map { item ->
                                                convertToUtcDailySeconds(
                                                    item
                                                )
                                            },
                                        )
                                    ).addOnSuccessListener {
                                        coroutineScope.launch {
                                            snackbarState.showSnackbar(
                                                "Change was successful",
                                                "ok",
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }.addOnFailureListener {
                                        coroutineScope.launch {
                                            snackbarState.showSnackbar(
                                                "Change was unsuccessful : ${it.message}",
                                                "ok",
                                                true,
                                                SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            },
                            alignment = Alignment.Center,
                            text = "Change",
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }

                    Row {
                        LazyColumn(modifier = Modifier.padding(it)) {
                            items(list.size) { index ->
                                Log.d("TIME", list[index].toString())
                                val toLocalTime = deconvertToDailySeconds(list[index])
                                val pair = convertTimeStampToHourAndMinute(toLocalTime)
                                var state = remember {
                                    TimePickerState(
                                        is24Hour = true,
                                        initialHour = pair.first,
                                        initialMinute = pair.second,
                                    )
                                }


                                LaunchedEffect(key1 = state.hour, key2 = state.minute) {
                                    list = list.mapIndexed { i1, time ->
                                        if (i1 == index) convertTimeToTimestamp(
                                            state.hour, state.minute
                                        ) else time

                                    }
                                }
                                Row {
                                    TimePicker(
                                        state = state,
                                        modifier = Modifier.padding(16.dp),
                                        colors = TimePickerDefaults.colors(
                                            timeSelectorSelectedContainerColor = universalAccent,
                                            timeSelectorUnselectedContainerColor = universalAccent,
                                            timeSelectorSelectedContentColor = universalPrimary,
                                            timeSelectorUnselectedContentColor = universalPrimary
                                        )
                                    )
                                }
                            }

                        }

                    }

                }


            }
        }
    }
}
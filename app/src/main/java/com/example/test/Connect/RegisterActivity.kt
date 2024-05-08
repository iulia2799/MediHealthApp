package com.example.test.Connect

import Models.Department
import Models.Doctor
import Models.Patient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.example.test.Components.CustomSwitch
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.FormSelector
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.emailPattern
import com.example.test.Components.passwordPattern
import com.example.test.Home
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.example.test.utils.DOCTORS
import com.example.test.utils.PATIENTS
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var departmentList = Department.values().map { it.displayName }
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ScreenR()
                }
            }
        }
    }

    @Preview
    @Composable
    fun ScreenR() {
        Surface(
            modifier = Modifier.fillMaxSize(), color = universalBackground
        ) {
            ContentR()
        }
    }

    @Composable
    fun ContentR() {
        val context = LocalContext.current
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var checked by remember { mutableStateOf(false) }
        var department by remember { mutableStateOf(departmentList[0]) }
        var passwordSame by remember {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    LargeTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        value = "Create a new account"
                    )
                }
            }
            Row {
                CustomTextField(
                    text = firstName,
                    labelValue = "First Name",
                    onTextChange = { newValue ->
                        firstName = newValue
                    })
                Spacer(modifier = Modifier.weight(1f))
                CustomTextField(
                    text = lastName,
                    labelValue = "Last Name",
                    onTextChange = { newValue ->
                        lastName = newValue
                    })
            }
            Row {
                CustomTextField(
                    text = email, labelValue = "Email", onTextChange = { newValue ->
                        email = newValue
                    }, pattern = emailPattern
                )
                Spacer(modifier = Modifier.weight(1f))
                CustomTextField(
                    text = password, labelValue = "Password", onTextChange = { newValue ->
                        password = newValue
                    }, type = "password", pattern = passwordPattern
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Box(modifier = Modifier.wrapContentSize(Alignment.Center)) {
                    CustomTextField(
                        text = confirmPassword,
                        labelValue = "Confirm Password",
                        onTextChange = { newValue ->
                            confirmPassword = newValue
                            passwordSame = password == confirmPassword
                        },
                        type = "password",
                        pattern = passwordPattern
                    )
                }

            }
            Row {
                CustomTextField(text = phone, labelValue = "Phone", onTextChange = { newValue ->
                    phone = newValue
                })
                Spacer(modifier = Modifier.weight(1f))
                CustomTextField(text = address, labelValue = "Address", onTextChange = { newValue ->
                    address = newValue
                })
                Spacer(modifier = Modifier.weight(1f))
            }
            if (!checked) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Box(modifier = Modifier.wrapContentSize(Alignment.Center)) {
                        CustomTextField(text = age, labelValue = "Age", onTextChange = { newValue ->
                            age = newValue
                        })
                    }
                }
            } else {
                Row {
                    CenteredBox {
                        FormSelector(
                            options = departmentList,
                            selectedOption = department,
                            onOptionSelected = {
                                department = it
                            },
                            modifier = Modifier
                                .width(350.dp)
                                .heightIn(max = 200.dp)
                        )
                    }

                }
            }
            Row {
                CenteredBox {
                    MediumTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        value = "I am a licensed specialist"
                    )
                }

            }
            Row {
                CenteredBox {
                    CustomSwitch(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center), checked
                    ) { newValue ->
                        checked = newValue
                    }
                }
            }
            Row {
                DefaultButton(
                    onClick = {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if (checked) {
                                val departmentValue = Department.values().find {
                                    it.displayName == department
                                }
                                val doc = Doctor(
                                    email = email,
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    address = address,
                                    department = departmentValue!!
                                )
                                db.collection(DOCTORS).add(doc).addOnCompleteListener {
                                    val localStorage = LocalStorage(context)
                                    localStorage.putUserDetails(
                                        true,
                                        it.result.id,
                                        departmentValue.ordinal,
                                        firstName,
                                        lastName
                                    )
                                    localStorage.loginUser()
                                    context.startActivity(Intent(context, Home::class.java))
                                }.addOnFailureListener {
                                    //oops
                                }
                            } else {
                                val p = Patient(
                                    email = email,
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    address = address,
                                    age = age.toInt()
                                )
                                db.collection(PATIENTS).add(p).addOnCompleteListener {
                                    val localStorage = LocalStorage(context)
                                    localStorage.putUserDetails(
                                        false, it.result.id, firstName, lastName
                                    )
                                    localStorage.loginUser()
                                    context.startActivity(Intent(context, Home::class.java))
                                }.addOnFailureListener {
                                    it.message?.let { it1 -> Log.e("OOPS", it1) }
                                }
                            }

                        }.addOnFailureListener { print("exception") }
                    },
                    Alignment.Center,
                    "Register",
                    Modifier
                        .height(100.dp)
                        .padding(20.dp)
                        .fillMaxWidth(),
                    enabled = passwordSame
                )
            }
        }
    }
}

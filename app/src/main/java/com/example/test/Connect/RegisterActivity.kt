package com.example.test.Connect

import Models.Department
import Models.Doctor
import Models.Patient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CenteredBox
import com.example.test.Components.CustomSwitch
import com.example.test.Components.CustomTextField
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.LoginPageEnter
import com.example.test.Components.MediumTextField
import com.example.test.Components.emailPattern
import com.example.test.Components.passwordPattern
import com.example.test.Home
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.UUID

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var departmentList = Department.values().toList()
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ContentR() {
        val context = LocalContext.current
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var checked by remember { mutableStateOf(false) }
        var department by remember { mutableStateOf(departmentList[0]) }
        var expanded by remember {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
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
                CustomTextField(text = firstName,
                    labelValue = "First Name",
                    onTextChange = { newValue ->
                        firstName = newValue
                    })
                Spacer(modifier = Modifier.weight(1f))
                CustomTextField(text = lastName,
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
            if(!checked) {
                Row (modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)){
                    Box(modifier = Modifier.wrapContentSize(Alignment.Center)) {
                        CustomTextField(text = age, labelValue = "Age", onTextChange = { newValue ->
                            age = newValue
                        })
                    }
                }
            } else {
                Row{
                    CenteredBox {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                value = department.displayName,
                                onValueChange = { Toast.makeText(context,department.displayName,Toast.LENGTH_LONG).show() },
                                label = { Text("Department") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                    unfocusedBorderColor = unfocusedLabelColor,
                                    focusedBorderColor = universalAccent,
                                    focusedLabelColor = universalAccent,
                                    unfocusedLabelColor = unfocusedLabelColor,
                                    textColor = Color.Black,
                                    containerColor = universalPrimary,
                                    errorLabelColor = universalError,
                                    errorBorderColor = universalError,
                                    disabledBorderColor = Color.DarkGray,
                                    disabledTextColor = Color.DarkGray
                                ),
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            ) {
                                departmentList.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text =  {
                                            Text(text = department.displayName)
                                        },
                                        onClick = {
                                            department = selectionOption
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
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
                                val uuid = UUID.randomUUID().toString()
                                if (checked) {
                                    val doc = Doctor(
                                        uid = uuid,
                                        email = email,
                                        password = password,
                                        firstName = firstName,
                                        lastName = lastName,
                                        phone = phone,
                                        address = address,
                                        department = department
                                    )
                                    db.collection("doctors").add(doc).addOnCompleteListener {
                                        val localStorage = LocalStorage(context)
                                        localStorage.putUserDetails(uuid, true)
                                        context.startActivity(Intent(context, Home::class.java))
                                    }.addOnFailureListener {
                                        //oops
                                    }
                                } else {
                                    val p = Patient(
                                        uid = uuid,
                                        email = email,
                                        password = password,
                                        firstName = firstName,
                                        lastName = lastName,
                                        phone = phone,
                                        address = address,
                                        age = age.toInt()
                                    )
                                    db.collection("patients").add(p).addOnCompleteListener {
                                        Log.d("SUCCESS", it.isSuccessful.toString())
                                        if(it.isSuccessful) {
                                            val localStorage = LocalStorage(context)
                                            localStorage.putUserDetails(uuid, false)
                                            context.startActivity(Intent(context, Home::class.java))
                                        }
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
                        .fillMaxWidth()
                )
            }
            Row {
                DefaultButton(
                    onClick = { LoginPageEnter(context) },
                    Alignment.Center,
                    "Login to existing account",
                    Modifier
                        .height(100.dp)
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

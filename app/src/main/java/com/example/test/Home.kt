package com.example.test

import Models.Doctor
import Models.Patient
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CenteredBox
import com.example.test.Components.DefaultButton
import com.example.test.Components.MonthCalendar
import com.example.test.Components.RegisterPageEnter
import com.example.test.Components.Welcome
import com.example.test.LocalStorage.LocalStorage
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.YearMonth
import java.util.*

class Home : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var datap: Patient
    private lateinit var datad: Doctor
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        db = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContent {
            setContent()
        }
    }

    @Composable
    @Preview
    fun setContent() {
        val context = LocalContext.current
        val local = LocalStorage(context)
        val ref = local.getRef()
        val type = local.getRole()
        if(type.equals(true)) {

        }
        AppTheme {
            // A surface container using the 'background' color from the theme
            HomeContent()
        }
    }

    @Composable
    fun HomeContent() {
        HomeScaffold()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScaffold() {
        val context = LocalContext.current
        Scaffold(topBar = {
            TopAppBar(
                modifier = Modifier.shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                ),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = appBarContainerColor,
                    titleContentColor = Color.Black,
                ),
                title = {
                    Welcome(name = "First Name")
                },
                actions = {
                    FloatingActionButton(
                        onClick = { /*CREATE INTENT*/ },
                        modifier = Modifier.padding(5.dp),
                        containerColor = universalBackground,
                        contentColor = universalPrimary
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = universalPrimary
                        )
                    }
                },

                )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TO DO CREATE INTENT*/ },
                contentColor = universalPrimary,
                containerColor = universalBackground
            ) {
                Icon(Icons.Default.MailOutline, contentDescription = "Message")
            }
        }) { padding ->
            Column(
                modifier = Modifier.padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row{
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "Prescriptions",
                        Modifier
                            .height(100.dp).width(200.dp)
                            .padding(20.dp)
                    )
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "History",
                        Modifier
                            .height(100.dp).width(200.dp)
                            .padding(20.dp)
                    )
                }
                Row {
                    CenteredBox(){
                        MonthCalendar(yearMonth = YearMonth.now())
                    }

                }
                //if type user patient
                Row {
                    DefaultButton(
                        onClick = {  },
                        Alignment.Center,
                        "Doctors",
                        Modifier
                            .height(100.dp).width(200.dp)
                            .padding(20.dp)
                    )
                    DefaultButton(
                        onClick = { RegisterPageEnter(context) },
                        Alignment.Center,
                        "Results",
                        Modifier
                            .height(100.dp).width(200.dp)
                            .padding(20.dp)
                    )
                }
            }
        }
    }


}

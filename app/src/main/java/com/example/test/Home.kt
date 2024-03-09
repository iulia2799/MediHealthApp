package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.appBarContainerColor
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContent()
        }
    }

    @Composable
    @Preview
    fun setContent() {
        val context = LocalContext.current
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
        var context = LocalContext.current
        var list = listOf<String>()
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
                        MonthCalendar(yearMonth = YearMonth.now(), list)
                    }

                }
                Row {
                    //more
                }
            }
        }
    }


}

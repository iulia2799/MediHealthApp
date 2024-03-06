package com.example.test.Connect

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CenteredBox
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.CustomSwitch
import com.example.test.Components.CustomTextField
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Connect.ui.theme.TestTheme
import com.example.test.ui.theme.universalBackground

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenR()
                }
            }
        }
    }
}

@Preview
@Composable
fun ScreenR() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = universalBackground
    ) {
        ContentR()
    }
}
@Composable
fun ContentR() {
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .wrapContentWidth(Alignment.CenterHorizontally)) {
        Row{
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight(Alignment.CenterVertically)) {
                LargeTextField(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center), value = "Create a new account")
            }
        }
        Row{
            CustomTextField(labelValue = "First Name")
            Spacer(modifier = Modifier.weight(1f))
            CustomTextField(labelValue = "Last Name")

        }
        Row{
            CustomTextField(labelValue = "Email")
            Spacer(modifier = Modifier.weight(1f))
            CustomTextField(labelValue = "Password")
        }
        Row{
            CustomTextField(labelValue = "Phone")
            Spacer(modifier = Modifier.weight(1f))
            CustomTextField(labelValue = "Address")
        }
        Row {
            CenteredBox {
                MediumTextField(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center), value = "I am a licensed specialist")
            }

        }
        Row {
            CenteredBox{
                CustomSwitch(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center))
            }
        }
        Row{
            CustomElevatedButton(onClick = { Register(context) }, Alignment.Center,
                "Register",
                Modifier
                    .height(100.dp)
                    .padding(20.dp)
                    .fillMaxWidth())
        }
        Row{
            CustomElevatedButton(onClick = { LoginToExistingAccount(context) }, Alignment.Center,
                "Login to existing account",
                Modifier
                    .height(100.dp)
                    .padding(20.dp)
                    .fillMaxWidth())
        }
    }
}

fun Register(context: Context){

}

fun LoginToExistingAccount(context: Context){
    context.startActivity(Intent(context, LoginActivity::class.java))
}
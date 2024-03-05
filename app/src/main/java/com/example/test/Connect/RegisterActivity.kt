package com.example.test.Connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomTextField
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

    }
}
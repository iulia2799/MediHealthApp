package com.example.test.symptomchecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
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
import com.example.test.Components.MediumTextField
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground

class CheckerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = universalBackground
                ) {

                }
            }
        }
    }
}

@Composable
@Preview
fun Content() {
    val context = LocalContext.current
    var optionsList by remember {
        mutableStateOf(emptyList<Int>())
    }
    var model = PredictionModel()
    //model.getModel()
    Scaffold(topBar = {
        InfoHeader()
    }) {
        Column(modifier = Modifier.padding(it)) {

        }
    }
}

@Composable
fun InfoHeader() {
    Row(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)) {
        MediumTextField(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally), value = "This symptom checker does not replace a licensed specialist \n For major emergencies, please call the emergency number.")
    }
}


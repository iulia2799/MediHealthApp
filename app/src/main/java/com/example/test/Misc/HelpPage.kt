package com.example.test.Misc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.myCustomFontFamily
import com.example.test.ui.theme.universalBackground

class HelpPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = universalBackground,
                ) {
                    Column(modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)) {
                        Text(text = "Welcome!", fontFamily = myCustomFontFamily, fontSize = 34.sp)
                        Text(text = "This a patient and doctor application. To get started, please login to your account or create a new one.", fontFamily = myCustomFontFamily, fontSize = 24.sp)
                        Text(text = "You will be able to find your primary doctor, make appointments and chat.", fontFamily = myCustomFontFamily, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
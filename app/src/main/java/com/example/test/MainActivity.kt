package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.GreetingUser
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = universalBackground
                ) {
                    Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                        Greeting("Iulia")
                    }

                    CustomElevatedButton(
                        { WriteSomething() },
                        Alignment.TopCenter,
                        "Click me !",
                        Modifier.padding(100.dp)
                    )
                }
            }
        }
    }
}

fun WriteSomething() {
    print("stop")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .padding(10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        GreetingUser(name, modifier)
    }

}
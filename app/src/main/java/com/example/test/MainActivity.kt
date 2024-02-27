package com.example.test

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.CustomElevatedButton
import com.example.test.Components.ElevatedButtonWithText
import com.example.test.Components.GreetingUser
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.LightGray
                ) {
                    Greeting("Iulia")
                    CustomElevatedButton(
                        { WriteSomething() },
                        Alignment.Center,
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.LightGray
        ) {
            Greeting("Iulia")
            CustomElevatedButton(
                { WriteSomething() },
                Alignment.Center,
                "Click me !",
                Modifier.padding(100.dp)
            )
        }
    }
}
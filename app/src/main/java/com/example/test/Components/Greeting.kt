package com.example.test.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.myCustomFontFamily

@Composable
fun GreetingUser(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier,
            fontFamily = myCustomFontFamily,
            fontSize = 34.sp,

        )
}

@Composable
fun Greeting() {
    Box(
        modifier = Modifier
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        AppName()
    }

}

@Composable
fun AppName(modifier: Modifier = Modifier) {
    Text(
        text = "MediHealth",
        modifier = modifier,
        fontFamily = myCustomFontFamily,
        fontSize = 34.sp,
        textAlign = TextAlign.Center
        )
}
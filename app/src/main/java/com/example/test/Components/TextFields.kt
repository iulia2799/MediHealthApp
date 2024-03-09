package com.example.test.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.jejugothicFamily

@Composable
fun LargeTextField(value: String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = value,
        fontFamily = jejugothicFamily,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun MediumTextField(modifier: Modifier, value: String) {
    Text(
        modifier = modifier,
        text = value,
        fontFamily = jejugothicFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Black
    )
}

@Composable
fun SmallTextField(value: String) {
    Text(
        text = value,
        fontFamily = jejugothicFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
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
        fontFamily = jejugothicFamily,
        fontSize = 34.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Welcome(modifier: Modifier = Modifier, name: String) {
    Text(
        text = "Welcome, ${name}!",
        modifier = modifier,
        fontFamily = jejugothicFamily,
        fontSize = 24.sp,
        textAlign = TextAlign.Left
    )
}
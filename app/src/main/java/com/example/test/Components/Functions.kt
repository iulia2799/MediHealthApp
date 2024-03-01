package com.example.test.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.ui.theme.myCustomFontFamily


@Composable
fun CustomElevatedButton(
    onClick: () -> Unit,
    alignment: Alignment,
    text: String,
    modifier: Modifier,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(10.dp,10.dp,10.dp,10.dp,10.dp), // Default elevation
    backgroundColor: Color = Color(0xFF8DC6CD),
    contentColor: Color = Color.Black,
    fontFamily: FontFamily = myCustomFontFamily,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = modifier,
        contentAlignment = alignment,
    ) {
        ElevatedButton(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
            elevation = elevation,
        ) {
            Text(text, style = TextStyle(fontFamily = fontFamily, fontWeight = fontWeight))
        }
    }
}
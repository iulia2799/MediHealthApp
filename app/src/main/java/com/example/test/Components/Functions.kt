package com.example.test.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.test.ui.theme.myCustomFontFamily
import com.example.test.ui.theme.universalBackground


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(labelValue: String) {
    var nameState = rememberSaveable { mutableStateOf("") }
    OutlinedTextField(modifier = Modifier
        .width(185.dp)
        .padding(10.dp).shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color.Black),
        shape = RoundedCornerShape(24.dp),
        value = nameState.value,
        onValueChange = { nameState.value = it},
        label = {
            Text(text = labelValue)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedLabelColor = Color.Green,
            unfocusedLabelColor = Color.Blue,
            textColor = Color.Red,
            containerColor = universalBackground,

        ),
    )
}
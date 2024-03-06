package com.example.test.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalError
import com.example.test.ui.theme.universalPrimary


@Composable
fun CustomElevatedButton(
    onClick: () -> Unit,
    alignment: Alignment,
    text: String,
    modifier: Modifier,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(
        10.dp, 10.dp, 10.dp, 10.dp, 10.dp
    ), // Default elevation
    backgroundColor: Color = universalPrimary,
    contentColor: Color = Color.Black,
    fontFamily: FontFamily = jejugothicFamily,
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

@Composable
fun CustomSwitch(modifier: Modifier) {
    var checked by remember { mutableStateOf(false) }
    Switch(modifier = modifier,
        checked = checked,
        onCheckedChange = { checked = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = universalPrimary,
            checkedTrackColor = unfocusedLabelColor,
            uncheckedThumbColor = universalAccent,
            uncheckedTrackColor = unfocusedLabelColor,
        ),
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(text: String = "", labelValue: String, onTextChange: (String) -> Unit = {}) {
    OutlinedTextField(
        modifier = Modifier
            .width(185.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(24.dp),
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = labelValue)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = unfocusedLabelColor,
            focusedBorderColor = universalAccent,
            focusedLabelColor = universalAccent,
            unfocusedLabelColor = unfocusedLabelColor,
            textColor = Color.Black,
            containerColor = universalPrimary,
            errorLabelColor = universalError,
            errorBorderColor = universalError,
            disabledBorderColor = Color.DarkGray,
            disabledTextColor = Color.DarkGray
        ),
    )
}

@Composable
fun CenteredBox(content: @Composable (BoxScope.() -> Unit)) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    ) {
        content()
    }
}
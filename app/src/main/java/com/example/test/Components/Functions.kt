package com.example.test.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.offWhite
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalError
import com.example.test.ui.theme.universalPrimary

var emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
var passwordPattern = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}"
var textPattern = ".*"
@Composable
fun DefaultButton(
    onClick: () -> Unit,
    alignment: Alignment,
    text: String,
    modifier: Modifier,
    backgroundColor: Color = universalPrimary,
    contentColor: Color = Color.Black,
    fontFamily: FontFamily = jejugothicFamily,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = modifier,
        contentAlignment = alignment,
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            )
        ) {
            Text(text, style = TextStyle(fontFamily = fontFamily, fontWeight = fontWeight))
        }
    }
}

@Composable
fun CustomSwitch(modifier: Modifier, checked: Boolean, onStateChange: (Boolean) -> Unit) {
    Switch(modifier = modifier,
        checked = checked,
        onCheckedChange = onStateChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = universalPrimary,
            checkedTrackColor = unfocusedLabelColor,
            uncheckedThumbColor = universalPrimary,
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
fun CustomTextField(text: String, labelValue: String, onTextChange: (String) -> Unit = {}, type: String = "text", pattern: String = textPattern) {
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
        keyboardOptions = when(type) {
            "password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
            else -> KeyboardOptions.Default
        },
        isError = !text.matches(pattern.toRegex())
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

@Composable
fun DefaultIconButton(onClick: () -> Unit, imageVector: ImageVector, description: String) {
    IconButton(onClick = onClick) {
        Icon(imageVector = imageVector, contentDescription = description, tint = universalPrimary)
    }
}
@Composable
fun CustomCardViewDark(modifier: Modifier,content: @Composable ()-> Unit, isSelected: Boolean = false){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if(isSelected) boldPrimary else universalAccent,
            contentColor = offWhite,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedOption,
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        onValueChange = { /* Handle text input if needed */ },
        label = { Text("Select your field of expertise") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { expanded = true }
            )
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

    DropdownMenu(
        modifier = Modifier
            .width(350.dp).heightIn(max = 200.dp),
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                       Text(text = option)
                },
                onClick = {
                    onOptionSelected(option)
                    expanded = false
                }
            )
        }
    }
}

package com.example.test.Components

import Models.Message
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.ui.theme.boldPrimary
import com.example.test.ui.theme.jejugothicFamily
import com.example.test.ui.theme.offWhite
import com.example.test.ui.theme.unfocusedLabelColor
import com.example.test.ui.theme.universalAccent
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalError
import com.example.test.ui.theme.universalPrimary

var emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
var passwordPattern = "(?=.*\\d)[A-Za-z\\d\\W]{8,}"
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
    fontWeight: FontWeight = FontWeight.Normal,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier,
        contentAlignment = alignment,
    ) {
        Button(
            enabled = enabled, onClick = onClick, colors = ButtonDefaults.buttonColors(
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

@Composable
fun MessageTextField(
    text: String,
    labelValue: String,
    onTextChange: (String) -> Unit = {},
    type: String = "text",
    pattern: String = textPattern,
    mods: Modifier = Modifier.padding(10.dp)
) {
    CustomTextField(text = text, labelValue = labelValue, onTextChange, type, pattern, mods)
}

@Composable
fun LongTextField(
    text: String,
    labelValue: String,
    onTextChange: (String) -> Unit = {},
    type: String = "text",
    pattern: String = textPattern,
    mods: Modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
) {
    CustomTextField(text = text, labelValue = labelValue, onTextChange, type, pattern, mods)
}

@Composable
fun CustomTextField(
    text: String,
    labelValue: String,
    onTextChange: (String) -> Unit = {},
    type: String = "text",
    pattern: String = textPattern,
    mods: Modifier = Modifier
        .width(185.dp)
        .padding(10.dp)
) {
    OutlinedTextField(
        modifier = mods,
        shape = RoundedCornerShape(24.dp),
        value = text,
        onValueChange = onTextChange,
        label = {
            Text(text = labelValue)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.DarkGray,
            errorTextColor = universalError,
            focusedContainerColor = universalPrimary,
            unfocusedContainerColor = universalPrimary,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = universalPrimary,
            cursorColor = universalAccent,
            errorCursorColor = universalError,
            unfocusedBorderColor = unfocusedLabelColor,
            focusedBorderColor = universalAccent,
            errorBorderColor = universalError,
            disabledBorderColor = Color.DarkGray,
            focusedLeadingIconColor = universalAccent,
            unfocusedLeadingIconColor = universalPrimary,
            disabledLeadingIconColor = Color.DarkGray,
            errorLeadingIconColor = universalError,
            focusedTrailingIconColor = universalAccent,
            unfocusedTrailingIconColor = universalPrimary,
            disabledTrailingIconColor = universalAccent,
            errorTrailingIconColor = Color.Red,
            focusedLabelColor = universalAccent,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = Color.DarkGray,
            errorLabelColor = universalAccent,
            focusedPlaceholderColor = universalAccent,
            unfocusedPlaceholderColor = universalPrimary,
            disabledPlaceholderColor = universalAccent,
            errorPlaceholderColor = universalError,
            focusedSupportingTextColor = universalAccent,
            unfocusedSupportingTextColor = universalPrimary,
            disabledSupportingTextColor = Color.DarkGray,
            errorSupportingTextColor = universalError,
            focusedPrefixColor = universalAccent,
            unfocusedPrefixColor = universalBackground,
            disabledPrefixColor = Color.Black,
            errorPrefixColor = universalError,
            focusedSuffixColor = universalAccent,
            unfocusedSuffixColor = Color.LightGray,
            disabledSuffixColor = Color.DarkGray,
            errorSuffixColor = universalError,
        )/*colors = OutlinedTextFieldDefaults.colors(
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
        )*/,
        keyboardOptions = when (type) {
            "password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
            "number" -> KeyboardOptions(keyboardType = KeyboardType.Number)
            else -> KeyboardOptions.Default
        },
        visualTransformation = when (type) {
            "password" -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        isError = when (pattern) {
            textPattern -> false
            else -> !text.matches(pattern.toRegex())
        }
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
fun CustomCardViewDark(
    modifier: Modifier, content: @Composable () -> Unit, isSelected: Boolean = false
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = if (isSelected) boldPrimary else universalAccent,
            contentColor = offWhite,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        )
    ) {
        content()
    }
}

@Composable
fun FormSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    text: String = "Select your field of expertise",
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedOption,
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        onValueChange = { /* Handle text input if needed */ },
        label = { Text(text) },
        trailingIcon = {
            Icon(imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { expanded = true })
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.DarkGray,
            errorTextColor = universalError,
            focusedContainerColor = universalPrimary,
            unfocusedContainerColor = universalPrimary,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = universalPrimary,
            cursorColor = universalAccent,
            errorCursorColor = universalError,
            unfocusedBorderColor = unfocusedLabelColor,
            focusedBorderColor = universalAccent,
            errorBorderColor = universalError,
            disabledBorderColor = Color.DarkGray,
            focusedLeadingIconColor = universalAccent,
            unfocusedLeadingIconColor = universalPrimary,
            disabledLeadingIconColor = Color.DarkGray,
            errorLeadingIconColor = universalError,
            focusedTrailingIconColor = universalAccent,
            unfocusedTrailingIconColor = universalPrimary,
            disabledTrailingIconColor = universalAccent,
            errorTrailingIconColor = Color.Red,
            focusedLabelColor = universalAccent,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = Color.DarkGray,
            errorLabelColor = universalAccent,
            focusedPlaceholderColor = universalAccent,
            unfocusedPlaceholderColor = universalPrimary,
            disabledPlaceholderColor = universalAccent,
            errorPlaceholderColor = universalError,
            focusedSupportingTextColor = universalAccent,
            unfocusedSupportingTextColor = universalPrimary,
            disabledSupportingTextColor = Color.DarkGray,
            errorSupportingTextColor = universalError,
            focusedPrefixColor = universalAccent,
            unfocusedPrefixColor = universalBackground,
            disabledPrefixColor = Color.Black,
            errorPrefixColor = universalError,
            focusedSuffixColor = universalAccent,
            unfocusedSuffixColor = Color.LightGray,
            disabledSuffixColor = Color.DarkGray,
            errorSuffixColor = universalError,
        )
    )

    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = { expanded = false }) {
        options.forEach { option ->
            DropdownMenuItem(text = {
                Text(text = option)
            }, onClick = {
                onOptionSelected(option)
                expanded = false
            })
        }
    }
}

@Composable
fun ReceivedMessage(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(boldPrimary), contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = convertMillisToExactDate(message.time),
                    modifier = Modifier.padding(10.dp),
                    fontSize = 12.sp,
                    fontFamily = jejugothicFamily,
                    color = universalAccent
                )
                Text(
                    text = message.text,
                    modifier = Modifier.padding(10.dp),
                    fontFamily = jejugothicFamily,
                    fontSize = 16.sp
                )
            }

        }

    }
}

@Composable
fun SentMessage(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(universalPrimary), contentAlignment = Alignment.CenterEnd
        ) {
            Column {
                Text(
                    text = convertMillisToExactDate(message.time),
                    modifier = Modifier.padding(10.dp),
                    fontSize = 12.sp,
                    fontFamily = jejugothicFamily,
                    color = universalAccent
                )
                Text(
                    text = message.text,
                    modifier = Modifier.padding(10.dp),
                    fontFamily = jejugothicFamily,
                    fontSize = 16.sp
                )
            }

        }

    }
}

@Composable
fun Header(username: String, onClick: () -> Unit = {}) {
    Row {
        LargeTextField(value = username,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start)
                .weight(1f)
                .clickable {
                    onClick()
                })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerCard(onSelection: (String) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })
    val selected = state.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        DefaultButton(
            onClick = {
                onSelection(selected)
                onDismiss()
            }, alignment = Alignment.Center, text = "Confirm", modifier = Modifier.padding(4.dp)
        )
    }, dismissButton = {
        DefaultButton(
            onClick = { onDismiss() },
            alignment = Alignment.Center,
            text = "Cancel",
            modifier = Modifier.padding(4.dp)
        )
    }) {
        DatePicker(state = state)
    }

}
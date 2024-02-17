package com.example.test.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ElevatedButtonWithText(onClick: () -> Unit, alignment: Alignment, text: String, modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        ElevatedButton(onClick = { onClick() }) {
            Text(text)
        }
    }
}
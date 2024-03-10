package com.example.test.Components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.test.Connect.LoginActivity
import com.example.test.Connect.RegisterActivity
import com.example.test.MainActivity

fun Back(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
}

fun LoginPageEnter(context: Context) {
    context.startActivity(Intent(context, LoginActivity::class.java))
}

fun RegisterPageEnter(context: Context) {
    context.startActivity(Intent(context, RegisterActivity::class.java))
}

@Composable
fun Listing(list: MutableList<Any>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp)
    ){
        items(list){ x -> CenteredBox {

        }}
    }
}
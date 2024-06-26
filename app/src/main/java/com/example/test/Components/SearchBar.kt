package com.example.test.Components

import Models.Department
import Models.Patient
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.test.LocalStorage.LocalStorage
import com.example.test.services.Search
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearch(
    content: @Composable (String, Patient, () -> Unit) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }
    var filter by remember { mutableStateOf(emptyMap<String, Patient>()) }
    var searchService = Search(LocalContext.current)
    val dep = LocalStorage(LocalContext.current).getDep()
    var ref = LocalStorage(LocalContext.current).getRef()

    LaunchedEffect(text) {
        delay(1000)
        Log.d("COROUTINE", "SCOPE")
        launch {
            var query = text
            if(dep == Department.GP.ordinal) {
                query = "$text $ref"
            }
            filter = searchService.retrievePatientValues(query)
        }
    }

    Row {
        SearchBar(modifier = Modifier.fillMaxWidth(), query = text, onQueryChange = {
            text = it
        }, onSearch = {
            //filter = filterCallback(text)
        }, active = active, onActiveChange = {
            active = it
        }, placeholder = {
            Text(text = "Search")
        }, leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
        }, trailingIcon = {
            if (active) {
                Icon(imageVector = Icons.Default.Clear,
                    contentDescription = "close",
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            active = false
                        }
                    })
            }

        }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        400.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                filter.forEach {
                    content(it.key,it.value) {
                        active = false
                    }
                }
            }
        }

    }
}
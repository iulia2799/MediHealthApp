package com.example.test.symptomchecker

import Models.Diseases.diseaseList
import Models.Diseases.symptomList
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.LargeTextField
import com.example.test.Components.MediumTextField
import com.example.test.Components.SmallTextField
import com.example.test.Components.toTitleCase
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.darkAccent
import com.example.test.ui.theme.darkPrimary
import com.example.test.ui.theme.darkTertiary
import com.example.test.ui.theme.universalBackground
import com.example.test.ui.theme.universalPrimary
import com.example.test.ui.theme.universalTertiary
import com.example.test.utils.EMERGENCY_NUMBERS
import kotlinx.coroutines.delay

class CheckerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = universalBackground
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
@Preview
fun Content() {
    val form = IntArray(132) { 0 }.toMutableList()
    var optionsList by remember {
        mutableStateOf(form)
    }

    var prediction by remember {
        mutableStateOf("")
    }

    var option by remember {
        mutableStateOf("")
    }
    val model = PredictionModel()
    model.getModel()
    Scaffold(topBar = {
        InfoHeader()
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()).heightIn(max = 1200.dp)
        ) {
            Row {
                DefaultButton(
                    onClick = { optionsList[option.toInt()] = 1 },
                    alignment = Alignment.Center,
                    text = "Add Symptom",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            Row {
                SimpleSearch(
                    data = symptomList,
                    filterCallback = ::searchByValue
                ) { key, callback ->
                    SearchItem(item = key, removable = false, onRemove = {
                        val index = symptomList.indexOf(it)
                        val newList = optionsList.toMutableList()
                        newList[index] = 0
                        optionsList = newList
                    }, onClick = {
                        val index = symptomList.indexOf(key)
                        option = index.toString()
                        val newList = optionsList.toMutableList()
                        newList[index] = 1
                        optionsList = newList
                        callback()
                    })
                }
            }
            Row {
                LazyColumn {
                    itemsIndexed(optionsList) { index, item ->
                        if (item != 0)
                            SearchItem(item = symptomList[index], removable = true, onRemove = {
                                val newList = optionsList.toMutableList()
                                newList[index] = 0
                                optionsList = newList
                            }, onClick = {
                                option = index.toString()
                                val newList = optionsList.toMutableList()
                                newList[option.toInt()] = 1
                                optionsList = newList
                            })
                    }
                }
            }
            Row {
                DefaultButton(
                    onClick = {
                        optionsList.forEach {
                            Log.d("INPUT", it.toString())
                        }
                        val results = model.interpret(optionsList)
                        if (results != null) {
                            results.forEach {
                                val biggest = it.maxOrNull()
                                val index = it.indexOfFirst { el ->
                                    el == biggest
                                }
                                Log.d("RESULT", biggest.toString())
                                Log.d("RESULT", diseaseList[index].name)
                                prediction = diseaseList[index].name
                            }
                        } else {
                            Log.d("results", "OOPS")
                        }
                    },
                    alignment = Alignment.Center,
                    text = "Check",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            if (prediction.isNotEmpty()) {
                Row {
                    LargeTextField(
                        value = "Result: $prediction",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

        }
    }
}

@Composable
fun InfoHeader() {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        val hyperLink = buildAnnotatedString {
            append("This symptom checker does not replace a licensed specialist \n For major emergencies, please call the emergency number.\n")
            pushStringAnnotation(tag = "URL", annotation = EMERGENCY_NUMBERS)
            withStyle(
                style = SpanStyle(
                    color = Color.Blue, fontWeight = FontWeight.Bold
                )
            ) {
                append("Emergency numbers world wide")
            }
            pop()
        }
        ClickableText(text = hyperLink, onClick = { offset ->
            hyperLink.getStringAnnotations(
                tag = "URL", start = offset, end = offset
            ).firstOrNull()?.let {
                Log.d("valid URL", it.item)
                uriHandler.openUri(it.item)
            }
        })
    }
}

@Composable
fun SearchItem(
    item: String,
    onClick: (String) -> Unit = {},
    removable: Boolean = false,
    onRemove: (String) -> Unit = {}
) {
    Card(
        colors = CardColors(
            contentColor = universalTertiary,
            containerColor = universalPrimary,
            disabledContentColor = Color.DarkGray,
            disabledContainerColor = Color.LightGray
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clickable { onClick(item) }, shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                MediumTextField(value = toTitleCase(item), modifier = Modifier, color = darkTertiary)
            }
            if (removable) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    TextButton(
                        onClick = { onRemove(item) },
                        colors = ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.DarkGray
                        )
                    ) {
                        MediumTextField(value = "Remove",modifier = Modifier)
                    }
                }
            }
        }
    }
}

fun searchByValue(data: List<String>, query: String): List<String> {
    return data.filter {
        it.contains(query, ignoreCase = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <K> SimpleSearch(
    data: List<K>,
    filterCallback: (List<K>, String) -> List<K>,
    content: @Composable (K, () -> Unit) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }
    var filter by remember { mutableStateOf(data) }
    LaunchedEffect(key1 = text) {
        delay(100)
        filter = filterCallback(data,text)
    }
    Row {
        SearchBar(modifier = Modifier.fillMaxWidth(), query = text, onQueryChange = {
            text = it
        }, onSearch = {
            filter = filterCallback(data, text)
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
                        600.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                filter.forEach {
                    content(it) {
                        active = false
                    }
                }
            }
        }

    }
}


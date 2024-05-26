package com.example.test.symptomchecker

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.MediumTextField
import com.example.test.Components.SmallTextField
import com.example.test.Components.filterSymptomList
import com.example.test.Components.toTitleCase
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground
import com.example.test.utils.EMERGENCY_NUMBERS

class CheckerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
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
    var form = IntArray(132) { 0 }.toMutableList()
    var optionsList by remember {
        mutableStateOf(form)
    }
    var viewOptions by remember {
        mutableStateOf(listOf(""))
    }

    var option by remember {
        mutableStateOf("")
    }
    var searchResults by remember {
        mutableStateOf(emptyMap<Int, String>())
    }
    var searchText by remember {
        mutableStateOf("")
    }
    val model = PredictionModel()
    model.getModel()

    LaunchedEffect(key1 = searchText) {
        searchResults = filterSymptomList(searchText)
    }

    Scaffold(topBar = {
        InfoHeader()
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for symptoms") },
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(10.dp)) {
                    items(searchResults.keys.toList()) {
                        searchResults[it]?.let { it1 ->
                            SearchItem(item = it1, onClick = { newValue ->
                                option = symptomList.indexOf(newValue).toString()
                                searchText = ""
                            })
                        }
                    }
                }
            }
            Row{
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(10.dp)) {
                    items(optionsList) {
                        SearchItem(item = symptomList[it], removable = true, onRemove = {
                            optionsList[it] = 0
                        })
                    }
                }
            }
            Row {
                DefaultButton(
                    onClick = {
                        val results = model.interpret(listOf(optionsList))
                        Log.d("results", results)
                    },
                    alignment = Alignment.Center,
                    text = "Check",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
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
    onRemove: () -> Unit = {}
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(item) }, shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row {
                SmallTextField(value = toTitleCase(item))
            }
            if (removable) {
                Row {
                    TextButton(onClick = { onRemove() }) {
                        SmallTextField(value = "Remove")
                    }
                }
            }
        }
    }
}


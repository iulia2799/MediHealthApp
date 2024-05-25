package com.example.test.symptomchecker

import Models.Diseases.symptomList
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.Components.DefaultButton
import com.example.test.Components.MediumTextField
import com.example.test.Components.SmallTextField
import com.example.test.Components.filterSymptomList
import com.example.test.Components.toTitleCase
import com.example.test.ui.theme.AppTheme
import com.example.test.ui.theme.universalBackground

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

                }
            }
        }
    }
}

@Composable
@Preview
fun Content() {
    val context = LocalContext.current
    var form = IntArray(132) { 0 }
    var optionsList by remember {
        mutableStateOf(form)
    }
    var viewOptions by remember {
        mutableStateOf(emptyList<String>())
    }
    var option by remember {
        mutableStateOf("")
    }
    var searchResults by remember {
        mutableStateOf(emptyMap<Int,String>())
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var model = PredictionModel()
    //model.getModel()

    LaunchedEffect(key1 = searchText) {
        searchResults = filterSymptomList(searchText)
    }

    Scaffold(topBar = {
        InfoHeader()
    }) {
        Column(modifier = Modifier.padding(it)) {
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
            Row{
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search for symptoms") },
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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
        }
    }
}

@Composable
fun InfoHeader() {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        MediumTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            value = "This symptom checker does not replace a licensed specialist \n For major emergencies, please call the emergency number."
        )
    }
}
@Composable
fun SearchItem(item: String, onClick: (String) -> Unit = {}) {
    val context = LocalContext.current
    Card( modifier = Modifier.fillMaxWidth().clickable { onClick(item) }, shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.padding(4.dp)) {
            SmallTextField(value = toTitleCase(item))
        }
    }
}


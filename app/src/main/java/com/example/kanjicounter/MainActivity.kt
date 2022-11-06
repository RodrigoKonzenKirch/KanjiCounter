package com.example.kanjicounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanjicounter.ui.theme.KanjiCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainActivityViewModel by viewModels()

        setContent {
            KanjiCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppScreen(viewModel: MainActivityViewModel) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val charsCountedToUi = viewModel.mCharactersCounted.observeAsState()

    AppScreenContent(
        textInput = text,
        resultList = charsCountedToUi ,
        onClearText = {
            text = TextFieldValue("")
            viewModel.updateText("")
            viewModel.updateCharacters()
        }
    ) {
        text = it
        viewModel.updateText(it.text)
        viewModel.updateCharacters()
    }
}

@Composable
fun AppScreenContent(
    textInput: TextFieldValue,
    resultList: State<List<CharCounter>?>,
    onClearText: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit
){

    Column(modifier = Modifier.padding(8.dp)) {

        TextField(
            value = textInput,
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = "Clear Text",
                    modifier = Modifier.clickable(onClick = onClearText)
                )
            },
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(0.7F)
                .fillMaxSize()
                .padding(vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.textPlaceholder))}
        )

        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
        ) {
            resultList.value?.let { CharsCounterList(it) }

        }
    }
}

@Composable
fun CharsCounterList(resultList: List<CharCounter>){
    LazyColumn(modifier = Modifier.fillMaxSize()){
        items(resultList) { row ->
            Text(text = "${row.char} : ${row.counter}",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

        }
    }
}
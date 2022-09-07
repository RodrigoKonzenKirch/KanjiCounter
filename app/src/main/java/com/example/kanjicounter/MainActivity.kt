package com.example.kanjicounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
    val scroll = rememberScrollState(0)

    val charsCountedToUi = viewModel.getCharactersCounted().observeAsState()

    Column(modifier = Modifier.padding(8.dp)) {

        Column {
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    viewModel.updateText(newText.text)
                    viewModel.charactersCountedUpdate()
                },
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.textPlaceholder))}
            )

            Text(
                text = charsCountedToUi.value ?: "",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize()
                    .verticalScroll(scroll)
            )
        }
    }
}

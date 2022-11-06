package com.example.kanjicounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
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
        textResult = charsCountedToUi ,
        onClearText = {
            text = TextFieldValue("")
            viewModel.updateText("")
            viewModel.updateCharacters()
        },
        onTextChange = {
            text = it
            viewModel.updateText(it.text)
            viewModel.updateCharacters()
        }
    )
}

@Composable
fun AppScreenContent(
    textInput: TextFieldValue,
    textResult: State<String?>,
    onClearText: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit
){
    val scroll = rememberScrollState(0)

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

        SelectionContainer(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
        ){
            Text(
                text = textResult.value ?: "",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.verticalScroll(scroll)
            )
        }
    }
}

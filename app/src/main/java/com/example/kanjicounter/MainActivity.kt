package com.example.kanjicounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

    val charsCountedToUi = viewModel.charactersCounted.observeAsState()
    val notoFontFamily = FontFamily(
        Font(R.font.notoserifsclight)
    )
    val charToBeHighlighted = viewModel.charToBeHighlighted.observeAsState()
    val highlightedText = viewModel.textHighlighted.observeAsState()
    AppScreenContent(
        textInput = text,
        resultList = charsCountedToUi ,
        customFontFamily = notoFontFamily,
        charToBeHighlighted = charToBeHighlighted,
        highlightedText = highlightedText,
        onClearText = {
            text = TextFieldValue("")
            viewModel.updateText("")
            viewModel.updateCharacters()
        },
        onTextChange = {
            text = it
            viewModel.updateText(it.text)
            viewModel.updateCharacters()
        },
        onClickListItem = {
            if (it == viewModel.charToBeHighlighted.value){
                viewModel.setCharToBeHighlighted(' ')
                viewModel.updateTextHighlight()
            } else {
                viewModel.setCharToBeHighlighted(it)
                viewModel.updateTextHighlight()
            }
        }
    )
}

@Composable
fun AppScreenContent(
    textInput: TextFieldValue,
    resultList: State<List<CharCounter>?>,
    customFontFamily: FontFamily,
    charToBeHighlighted: State<Char?>,
    highlightedText: State<AnnotatedString?>,
    onClearText: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onClickListItem: (Char) -> Unit
){
    val defaultPadding = 8.dp
    Column {

        Surface(
            modifier = Modifier
                .weight(0.7F)
                .fillMaxSize()
                .padding(vertical = defaultPadding)
        ) {
            EditHighlightText(
                textInput = textInput,
                customFontFamily = customFontFamily,
                charToBeHighlighted = charToBeHighlighted,
                highlightedText = highlightedText,
                onClearText = onClearText,
                onTextChange = onTextChange
            )
        }

        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
                .padding(defaultPadding)
        ) {
            resultList.value?.let { CharsCounterList(it, customFontFamily, onClickListItem) }

        }
    }
}

@Composable
fun EditHighlightText(
    textInput: TextFieldValue,
    customFontFamily: FontFamily,
    charToBeHighlighted: State<Char?>,
    highlightedText: State<AnnotatedString?>,
    onClearText: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit

){
    val textFontSize = 18.sp
    if (charToBeHighlighted.value == ' ' || charToBeHighlighted.value == null){
        TextField(
            modifier = Modifier
                .fillMaxSize(),
            value = textInput,
            textStyle = TextStyle(fontSize = textFontSize, fontFamily = customFontFamily),
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = "Clear Text",
                    modifier = Modifier.clickable(onClick = onClearText)
                )
            },
            onValueChange = onTextChange,

            placeholder = { Text(stringResource(R.string.textPlaceholder))}
        )

    } else{
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            text = highlightedText.value?:AnnotatedString(""),
            fontSize = textFontSize,
            fontFamily = customFontFamily
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharsCounterList(resultList: List<CharCounter>, customFontFamily: FontFamily, onClickListItem: (Char) -> Unit){
    var selectedIndex by rememberSaveable { mutableStateOf(-1) }

    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(resultList) { index, row ->
            Card(
                modifier = Modifier
                    .padding(4.dp),
                onClick = {
                    onClickListItem(row.char)
                    selectedIndex = if (selectedIndex == index){
                        -1
                    } else{
                        index
                    }
                          },
                elevation = 4.dp,
                border = if (index == selectedIndex){ BorderStroke(2.dp, Color.Blue) } else { null }
            ) {
                Text(
                    text = "${row.char} : ${row.counter}",
                    fontFamily = customFontFamily,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
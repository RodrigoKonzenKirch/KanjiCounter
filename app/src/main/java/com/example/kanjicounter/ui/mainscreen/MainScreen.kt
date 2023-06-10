package com.example.kanjicounter.ui.mainscreen

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
import com.example.kanjicounter.R

@Composable
fun AppScreen(viewModel: MainScreenViewModel) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val charsCountedToUi = viewModel.charactersCounted.observeAsState()
    val notoFontFamily = FontFamily(
        Font(R.font.notoserifsclight)
    )
    val charToBeHighlighted = viewModel.charToBeHighlighted.observeAsState()
    val highlightedText = viewModel.textHighlighted.observeAsState()
    val selectedListIndex = viewModel.selectedListIndex.observeAsState()
    AppScreenContent(
        textInput = text,
        resultList = charsCountedToUi ,
        customFontFamily = notoFontFamily,
        charToBeHighlighted = charToBeHighlighted,
        highlightedText = highlightedText,
        selectedListIndex = selectedListIndex,
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
        onClickListItem = { char, int ->
            if (char == viewModel.charToBeHighlighted.value){
                viewModel.setCharToBeHighlighted(' ')
                viewModel.updateTextHighlight()
                viewModel.updateSelectedListIndex(-1)
            } else {
                viewModel.setCharToBeHighlighted(char)
                viewModel.updateTextHighlight()
                viewModel.updateSelectedListIndex(int)
            }
        },
        onTapHighlightedText = {
            viewModel.setCharToBeHighlighted(' ')
            viewModel.updateTextHighlight()
            viewModel.updateSelectedListIndex(-1)
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
    selectedListIndex: State<Int?>,
    onClearText: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onClickListItem: (Char, Int) -> Unit,
    onTapHighlightedText: () -> Unit
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
                onTextChange = onTextChange,
                onTapHighlightedText = onTapHighlightedText
            )
        }

        Surface(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
                .padding(defaultPadding)
        ) {
            resultList.value?.let { CharsCounterList(selectedListIndex, it, customFontFamily, onClickListItem) }

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
    onTextChange: (TextFieldValue) -> Unit,
    onTapHighlightedText: () -> Unit

){
    val textFontSize = 18.sp
    if (charToBeHighlighted.value == ' ' || charToBeHighlighted.value == null){
        TextField(
            modifier = Modifier
                .fillMaxSize(),
            value = textInput,
            textStyle = TextStyle(fontSize = textFontSize, fontFamily = customFontFamily),
            trailingIcon = {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Clear Text",
                    modifier = Modifier.clickable(onClick = onClearText)
                )
            },
            onValueChange = onTextChange,

            placeholder = { Text(stringResource(R.string.textPlaceholder)) }
        )

    } else{
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .clickable { onTapHighlightedText() },
            text = highlightedText.value?: AnnotatedString(""),
            fontSize = textFontSize,
            fontFamily = customFontFamily
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharsCounterList(
    selectedListIndex: State<Int?>,
    resultList: List<CharCounter>,
    customFontFamily: FontFamily,
    onClickListItem: (Char, Int) -> Unit
)
{


    LazyColumn(modifier = Modifier.fillMaxSize()){
        itemsIndexed(resultList) { index, row ->
            Card(
                modifier = Modifier
                    .padding(4.dp),
                onClick = {
                    onClickListItem(
                        row.char,
                        if (selectedListIndex.value == index){
                            -1
                        } else{
                            index
                        }
                    )

                },
                elevation = 4.dp,
                border = if (index == selectedListIndex.value){ BorderStroke(2.dp, Color.Blue) } else { null }
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
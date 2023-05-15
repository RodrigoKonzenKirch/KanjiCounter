package com.example.kanjicounter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val invalidChars = "１２３４５６７８９０1234567890\n\r\"\'“”あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわゐゔゑをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽゃゅょっアイウエオャュョッァィェゥォヵヶカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヰヱヲヰンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポヴ｛｝{}（）()［］【】[]&,.;:/-+×÷、，゠＝=…‥。「」『』《》〝〟⟨⟩〜：！!？?＿＠＃＄％%∟°＆＊・•~^ー 　"
    private var _text = MutableLiveData<String>()
    private var _charactersCounted = MutableLiveData<List<CharCounter>>()
    private var _charToBeHighlighted = MutableLiveData<Char>()
    var charToBeHighlighted = _charToBeHighlighted
        private set
    var mCharactersCounted: LiveData<List<CharCounter>>
        private set

    var textHighlighted = MutableLiveData<AnnotatedString>()


    init {
        _text.value = ""
        _charToBeHighlighted.value = ' '
        _charactersCounted.value = listOf()
        mCharactersCounted = _charactersCounted
        textHighlighted.value = highlightCharOnText(_text.value, _charToBeHighlighted.value)
    }

    fun setCharToBeHighlighted(char: Char){
        _charToBeHighlighted.value = char
    }

    fun updateText(newText: String){
        _text.value = newText
    }

    fun updateTextHighlight(){
        textHighlighted.value = highlightCharOnText(_text.value, _charToBeHighlighted.value)
    }

    fun updateCharacters() {

        var charsFromText = removeDuplicates(_text.value!!)

        charsFromText = removeInvalidChars(charsFromText, invalidChars)

        val mapOfCharsNumberOfOccurrences = countOccurrencesOfCharInText(charsFromText, _text.value!!)

        val sortedMapOfCharsNumberOfOccurrences = sortMapOfCharsByDescending(mapOfCharsNumberOfOccurrences)

        _charactersCounted.value = sortedMapOfCharsNumberOfOccurrences
    }

    private fun removeDuplicates(text:String): MutableSet<Char>{
        val charsWithNoDuplicates = mutableSetOf<Char>()

        for (char in text){
            charsWithNoDuplicates.add(char)
        }

        return charsWithNoDuplicates
    }

    private fun countOccurrencesOfCharInText(setOfChars: Set<Char>, text: String): MutableMap<Char, Int>{
        val mapOfCharsNumberOfOccurrences = mutableMapOf<Char, Int>()

        for (char in setOfChars){
            mapOfCharsNumberOfOccurrences.put(char, text.count{ it == char})
        }

        return mapOfCharsNumberOfOccurrences.toSortedMap()
    }

    private fun removeInvalidChars(setOfChars: MutableSet<Char>, invalidChars: String): MutableSet<Char>{
        setOfChars.removeAll(invalidChars.asSequence().toSet())
        return setOfChars
    }

    private fun sortMapOfCharsByDescending(mapOfChars: MutableMap<Char, Int>): List<CharCounter> {
        val resultList = mutableListOf<CharCounter>()
        mapOfChars.entries.sortedByDescending { it.value }.associate { it.toPair() }.forEach{ entry -> resultList.add(
            CharCounter(entry.key, entry.value)
        )}
        return resultList
    }
}

data class CharCounter(val char: Char, val counter: Int)

fun highlightCharOnText(text: String?, charToBeHighlighted: Char?): AnnotatedString{

    val result: AnnotatedString = if (text.isNullOrBlank()){
        AnnotatedString("")
    } else {
        if (charToBeHighlighted == null || charToBeHighlighted == ' '){
            AnnotatedString(text)
        } else {
            highlightText(text, charToBeHighlighted)
        }
    }
    return result
}

private fun highlightText(text: String, charToBeHighlighted: Char):AnnotatedString{
    val indexesCharInText = mutableListOf<Int>()

    // Get indexes of words in the string containing the text to be highlighted
    var index = text.indexOf(charToBeHighlighted, 0)
    while (index != -1){
        indexesCharInText.add(index)
        index = text.indexOf(charToBeHighlighted, index + 1)
    }

    // Generate list containing indexes to be highlighted
    val listOfSpanStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
    indexesCharInText.forEach{
        listOfSpanStyles.add(AnnotatedString.Range(SpanStyle(background = Color.Yellow), it, it + 1))
    }
    return AnnotatedString(text, spanStyles = listOfSpanStyles)
}

//
//private fun highlightText(
//    textToBeHighlighted: String,
//    vocabularyList: List<Vocabulary>,
//    highlightColor: String
//): SpannableString {
//    // Get indexes of words in the string containing the text to be highlighted
//    val indexesAndWordSizes = mutableListOf<Pair<Int, Int>>()
//
//    if (vocabularyList.isNotEmpty()) {
//        vocabularyList.forEach {
//            var index = textToBeHighlighted.indexOf(it.vocabularyContent, 0)
//            while (index != -1) {
//                indexesAndWordSizes.add(Pair(index, it.vocabularyContent.length))
//                index = textToBeHighlighted.indexOf(it.vocabularyContent, index + 1)
//            }
//        }
//    }
//
//    // Highlight words using list of indexes of each word
//    val result = SpannableString(textToBeHighlighted)
//    indexesAndWordSizes.forEach {
//        result.setSpan(
//            BackgroundColorSpan(Color.parseColor(highlightColor)),
//            it.first,
//            it.first + it.second,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//    }
//    return result
//}

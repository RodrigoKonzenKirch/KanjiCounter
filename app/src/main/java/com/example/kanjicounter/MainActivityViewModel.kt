package com.example.kanjicounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val invalidChars = "１２３４５６７８９０1234567890\n\rあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわゐゔゑをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽゃゅょっアイウエオャュョッァィェゥォヵヶカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヰヱヲヰンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポヴ｛｝{}（）()［］【】[]&,.;:/-+、，゠＝=…‥。「」『』《》〝〟⟨⟩〜：！!？?＿＠＃＄％%∟°＆＊・•~^ー 　"
    private var _text = MutableLiveData<String>()
    private var _charactersCounted = MutableLiveData<String>()

    val mCharactersCounted: LiveData<String>

    init {
        _text.value = ""
        _charactersCounted.value = ""
        mCharactersCounted = _charactersCounted
    }

    fun updateText(newText: String){
        _text.value = newText
    }

    fun updateCharacters() {

        var charsFromText = removeDuplicates(_text.value!!)

        charsFromText = removeInvalidChars(charsFromText, invalidChars)

        val mapOfCharsNumberOfOccurrences = countOccurrencesOfCharInText(charsFromText, _text.value!!)

        val sortedMapOfCharsNumberOfOccurrences = sortMapOfCharsByDescending(mapOfCharsNumberOfOccurrences)

        _charactersCounted.value = formatCharsForPresentation(sortedMapOfCharsNumberOfOccurrences)
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

    private fun formatCharsForPresentation(orderedMapOfCharsNumberOfOccurrences: Map<Char, Int>): String{
        var valuesFormattedIntoString = ""
        for (item in orderedMapOfCharsNumberOfOccurrences){
            valuesFormattedIntoString += "${item.key} ${item.value}\n"
        }
        return valuesFormattedIntoString
    }

    private fun sortMapOfCharsByDescending(mapOfChars: MutableMap<Char, Int>): Map<Char, Int> {
        return mapOfChars.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }
}
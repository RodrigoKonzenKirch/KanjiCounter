package com.example.kanjicounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val invalidChars = "１２３４５６７８９０1234567890\n\r\"\'“”あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわゐゔゑをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽゃゅょっアイウエオャュョッァィェゥォヵヶカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヰヱヲヰンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポヴ｛｝{}（）()［］【】[]&,.;:/-+×÷、，゠＝=…‥。「」『』《》〝〟⟨⟩〜：！!？?＿＠＃＄％%∟°＆＊・•~^ー 　"
    private var _text = MutableLiveData<String>()
    private var _charactersCounted = MutableLiveData<List<CharCounter>>()

    val mCharactersCounted: LiveData<List<CharCounter>>

    init {
        _text.value = ""
        _charactersCounted.value = listOf()
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
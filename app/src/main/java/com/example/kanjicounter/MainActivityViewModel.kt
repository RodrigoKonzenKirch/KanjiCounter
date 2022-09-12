package com.example.kanjicounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private var _text = MutableLiveData<String>()
    private var _charactersCounted = MutableLiveData<String>()

    init {
        _text.value = ""
        _charactersCounted.value = ""
    }

    fun updateText(newText: String){
        _text.value = newText
    }

    fun getCharactersCounted(): LiveData<String> {
        return _charactersCounted
    }

    fun charactersCountedUpdate() {

        val textNotDuplicated = removeDuplicates(_text.value!!)
        val mapOfCharsNumberOfOccurrences = countOccurrencesOfCharInText(textNotDuplicated, _text.value!!)
        val orderedMapOfCharsNumberOfOccurrences = mapOfCharsNumberOfOccurrences.entries.sortedByDescending { it.value }.associate { it.toPair() }

        var valuesFormattedIntoString = ""
        for (item in orderedMapOfCharsNumberOfOccurrences){
            valuesFormattedIntoString += "${item.key} ${item.value}\n"
        }

        _charactersCounted.value = valuesFormattedIntoString
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
}
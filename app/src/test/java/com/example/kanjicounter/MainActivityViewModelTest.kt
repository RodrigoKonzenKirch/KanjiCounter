package com.example.kanjicounter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.kanjicounter.ui.mainscreen.CharCounter
import com.example.kanjicounter.ui.mainscreen.MainActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MainActivityViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private val viewModel = MainActivityViewModel()

    @Test
    fun updateCharacters() = runBlocking{
        val input = "aaa"
        val expected = CharCounter('a', 3)

        viewModel.updateText(input)
        viewModel.updateCharacters()
        assertEquals(expected, viewModel.charactersCounted.value)

    }
}
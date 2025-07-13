package com.stephenbrough.jetpack_learning.harry_potter_list_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.Book
import com.stephenbrough.jetpack_learning.domain.HarryPotterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HarryPotterListViewModel @Inject constructor(
    private val harryPotterRepository: HarryPotterRepository
): ViewModel(){

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books
        .onStart {
            val books = harryPotterRepository.getBooks()
            _books.value = books
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
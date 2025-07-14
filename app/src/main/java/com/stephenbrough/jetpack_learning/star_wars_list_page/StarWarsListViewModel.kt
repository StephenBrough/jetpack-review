package com.stephenbrough.jetpack_learning.star_wars_list_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.StarWarsMovie
import com.stephenbrough.jetpack_learning.domain.StarWarsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StarWarsListViewModel @Inject constructor(
    private val starWarsRepository: StarWarsRepository
) : ViewModel() {

    private var _movies = MutableStateFlow<List<StarWarsMovie>>(emptyList())
    val movies = _movies
        .onStart {
            starWarsRepository.getMovies()?.let {
                _movies.value = it
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
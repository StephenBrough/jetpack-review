package com.stephenbrough.jetpack_learning.star_wars_details_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephenbrough.jetpack_learning.domain.StarWarsMovieDetail
import com.stephenbrough.jetpack_learning.domain.StarWarsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = StarWarsDetailsViewModel.DetailsFactory::class)
class StarWarsDetailsViewModel @AssistedInject constructor(
    private val starWarsRepository: StarWarsRepository,
    @Assisted private val movieId: Int
) : ViewModel() {
    private var _state = MutableStateFlow<StarWarsDetailsUiState>(StarWarsDetailsUiState.Loading)
    val state = _state
        .onStart {
            _state.value = StarWarsDetailsUiState.Loading

            val result = starWarsRepository.getMovie(movieId)
            result.fold(
                { movie ->
                    _state.value = StarWarsDetailsUiState.Success(movie)
                },
                { exception ->
                    _state.value = StarWarsDetailsUiState.ErrorState(exception.message ?: "Unknown error")
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StarWarsDetailsUiState.Loading
        )

    @AssistedFactory
    interface DetailsFactory {
        fun create(movieId: Int): StarWarsDetailsViewModel
    }
}

sealed class StarWarsDetailsUiState {
    data class Success(val movie: StarWarsMovieDetail) : StarWarsDetailsUiState()
    object Loading : StarWarsDetailsUiState()
    data class ErrorState(val message: String) : StarWarsDetailsUiState()
}

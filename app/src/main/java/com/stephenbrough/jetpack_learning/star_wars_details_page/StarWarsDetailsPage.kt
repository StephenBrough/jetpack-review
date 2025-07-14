package com.stephenbrough.jetpack_learning.star_wars_details_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.domain.StarWarsMovieDetail

@Composable
fun StarWarsDetailsPage(
    modifier: Modifier = Modifier,
    id: String
) {
    val viewModel =
        hiltViewModel<StarWarsDetailsViewModel, StarWarsDetailsViewModel.DetailsFactory> { factory ->
            factory.create(id)
        }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentState = state

    when (currentState) {
        is StarWarsDetailsUiState.ErrorState -> ErrorScreen(
            modifier,
            errorMessage = currentState.message
        )

        StarWarsDetailsUiState.Loading -> LoadingScreen(modifier)
        is StarWarsDetailsUiState.Success -> StarWarsMovieDetailContent(
            modifier,
            movie = currentState.movie
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, errorMessage: String) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = errorMessage)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StarWarsMovieDetailContent(
    modifier: Modifier = Modifier,
    movie: StarWarsMovieDetail
) {
    val sharedScope = LocalNavSharedTransitionScope.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
//                        with(sharedScope) {
//                            AsyncImage(
//                                modifier = Modifier.sharedElement(
//                                    sharedScope.rememberSharedContentState("swMovieTitle_${movie?.title}"),
//                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                                ),
//                                model = book.cover,
//                                contentDescription = "Book cover"
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.padding(start = 8.dp))
        Column {
//                    with(sharedScope) {
            Text(
                text = movie?.title ?: "NO TITLE",
                style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.sharedElement(
//                                sharedScope.rememberSharedContentState("movieTitle_${movie.title}"),
//                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                            ),
            )
//                    }
//                    with(sharedScope) {
            Text(
                text = movie?.director ?: "NO DIRECTOR",
                style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.sharedElement(
//                                sharedScope.rememberSharedContentState("movieDirector_${movie.title}"),
//                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                            ),
            )
//                    }
        }
    }

}
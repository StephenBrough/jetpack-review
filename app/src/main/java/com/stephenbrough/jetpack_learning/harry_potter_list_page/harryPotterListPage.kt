package com.stephenbrough.jetpack_learning.harry_potter_list_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.domain.Book

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HarryPotterListPage(
    modifier: Modifier = Modifier,
    onItemClick: (Book) -> Unit
) {
    val viewModel = hiltViewModel<HarryPotterListViewModel>()
    val harryPotterBooks by viewModel.books.collectAsStateWithLifecycle()
    val sharedScope = LocalNavSharedTransitionScope.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        harryPotterBooks.forEach { book ->
            item(key = book.title) {
                Card(
                    onClick = { onItemClick(book) },
                    modifier = Modifier.fillMaxWidth(),
                    colors =CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        with(sharedScope) {
                            AsyncImage(
                                modifier = Modifier.sharedElement(
                                    sharedScope.rememberSharedContentState("bookCover_${book.title}"),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                                model = book.cover,
                                contentDescription = "Book cover"
                            )
                        }

                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Column {
                            with(sharedScope) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.sharedElement(
                                        sharedScope.rememberSharedContentState("bookTitle_${book.title}"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    ),
                                )
                            }
                            with(sharedScope) {
                                Text(
                                    text = book.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.sharedElement(
                                        sharedScope.rememberSharedContentState("bookDescription_${book.title}"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}
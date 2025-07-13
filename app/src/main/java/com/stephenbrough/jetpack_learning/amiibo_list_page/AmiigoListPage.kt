package com.stephenbrough.jetpack_learning.amiibo_list_page

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
import com.stephenbrough.jetpack_learning.domain.Amiibo

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AmiigoListPage(
    modifier: Modifier = Modifier,
    onItemClick : (Amiibo) -> Unit
) {
    var viewModel = hiltViewModel<AmiiboListViewModel>()
    val amiibos by viewModel.amiibos.collectAsStateWithLifecycle()

    val sharedScope = LocalNavSharedTransitionScope.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        amiibos.forEach { amiibo ->
            item(key = amiibo.name) {
                Card(
                    onClick = { onItemClick(amiibo) },
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
                                    sharedScope.rememberSharedContentState("amiiboImage_${amiibo.name}"),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                                model = amiibo.image,
                                contentDescription = "Book cover"
                            )
                        }

                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Column {
                            with(sharedScope) {
                                Text(
                                    text = amiibo.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.sharedElement(
                                        sharedScope.rememberSharedContentState("amiiboName_${amiibo.name}"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    ),
                                )
                            }
//                            with(sharedScope) {
//                                Text(
//                                    text = book.description,
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    modifier = Modifier.sharedElement(
//                                        sharedScope.rememberSharedContentState("bookDescription_${book.title}"),
//                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                                    ),
//                                )
//                            }
                        }
                    }
                }
            }
        }

    }

}
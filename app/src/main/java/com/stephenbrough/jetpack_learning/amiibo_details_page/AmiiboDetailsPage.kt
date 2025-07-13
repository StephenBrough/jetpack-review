package com.stephenbrough.jetpack_learning.amiibo_details_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.domain.Amiibo

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AmiiboDetailsPage(
    modifier: Modifier = Modifier,
    amiibo: Amiibo
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val sharedScope = LocalNavSharedTransitionScope.current

        Text("Content here")
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
        Spacer(modifier = Modifier.height(24.dp))
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
//        with(sharedScope) {
//            Text(
//                text = book.description,
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.sharedElement(
//                    sharedScope.rememberSharedContentState("bookDescription_${book.title}"),
//                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                ),
//            )
//        }


    }


}
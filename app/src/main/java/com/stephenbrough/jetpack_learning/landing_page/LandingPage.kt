package com.stephenbrough.jetpack_learning.landing_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LandingPage(modifier: Modifier = Modifier) {
    val sharedScope = LocalNavSharedTransitionScope.current
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        with(sharedScope) {
            Text(
                "Jetpack Compose Playground",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.sharedElement(
                    sharedScope.rememberSharedContentState("title"),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            )
        }
    }
}
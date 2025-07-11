package com.stephenbrough.jetpack_learning.landing_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(modifier: Modifier = Modifier) {
    val sharedScope = LocalNavSharedTransitionScope.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
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
                })
        }

    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
        }
    }
}
package com.stephenbrough.jetpack_learning.settings_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sharedScope = LocalNavSharedTransitionScope.current

    Box(
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.NavigateBefore,
                            contentDescription = "Back",
                            modifier = Modifier.clickable {
                                onBack()
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        with(sharedScope) {
                            Text(
                                "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.sharedElement(
                                    sharedScope.rememberSharedContentState("settings"),
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
}
package com.stephenbrough.jetpack_learning.landing_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavAction
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.util.navigation.TOP_LEVEL_ROUTES
import kotlinx.coroutines.launch

sealed class NavDrawerActions {
    object SettingsAction : NavDrawerActions()
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun LandingPage(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onNavDrawerAction: (NavDrawerActions) -> Unit
) {
    val sharedScope = LocalNavSharedTransitionScope.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel: LandingPageViewModel = hiltViewModel()
    val harryPotterBooks by viewModel.books.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = {
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
                            },
                            selected = false,
                            onClick = {
                                onNavDrawerAction(NavDrawerActions.SettingsAction)
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout"
                                )
                            },
                            label = {
                                Text(
                                    "Logout",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            },
                            selected = false,
                            onClick = { onLogout() }
                        )

                    }

                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
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
            },
            bottomBar = {
                NavigationBar/*(windowInsets = NavigationBarDefaults.windowInsets) */{
                    TOP_LEVEL_ROUTES.forEach { topLevelRoute ->
//                        val isSelected = topLevelRoute ==
                    }
                }
            }

        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                harryPotterBooks.forEach { book ->
                    item {
                        Card(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth(),
                            colors =CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                AsyncImage(model = book.cover, contentDescription = "Book cover")
                                Spacer(modifier = Modifier.padding(start = 8.dp))
                                Column {
                                    Text(text = book.title, style = MaterialTheme.typography.titleLarge)
                                    Text(text = book.description, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
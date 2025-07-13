package com.stephenbrough.jetpack_learning.landing_page

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.stephenbrough.jetpack_learning.MainActivity.Companion.LocalNavSharedTransitionScope
import com.stephenbrough.jetpack_learning.amiibo_list_page.AmiigoListPage
import com.stephenbrough.jetpack_learning.domain.Book
import com.stephenbrough.jetpack_learning.harry_potter_details_page.HarryPotterDetailsPage
import com.stephenbrough.jetpack_learning.harry_potter_list_page.HarryPotterListPage
import com.stephenbrough.jetpack_learning.util.navigation.AmiiboListRoute
import com.stephenbrough.jetpack_learning.util.navigation.HarryPotterDetailRoute
import com.stephenbrough.jetpack_learning.util.navigation.HarryPotterListRoute
import com.stephenbrough.jetpack_learning.util.navigation.LoadingRoute
import com.stephenbrough.jetpack_learning.util.navigation.TOP_LEVEL_ROUTES
import com.stephenbrough.jetpack_learning.util.navigation.TopLevelBackStack
import kotlinx.coroutines.launch

sealed class NavDrawerActions {
    object SettingsAction : NavDrawerActions()
}

@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
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

    val topLevelBackStack by rememberSaveable(stateSaver = TopLevelBackStack.Saver()) {
        mutableStateOf(TopLevelBackStack(HarryPotterListRoute))
    }

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
                NavigationBar {
                    TOP_LEVEL_ROUTES.forEach { topLevelRoute ->
                        val isSelected = topLevelRoute == topLevelBackStack.topLevelKey
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                topLevelBackStack.addTopLevel(topLevelRoute)
                            },

                            icon = {
                                Icon(topLevelRoute.icon, contentDescription = null)
                            }
                        )
                    }
                }
            }

        ) { innerPadding ->
            NavDisplay(
                backStack = topLevelBackStack.backStack,
                onBack = {
                    topLevelBackStack.removeLast()
                },
                entryProvider = entryProvider {
                    entry<LoadingRoute> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(innerPadding)
                            )
                        }
                    }
                    entry<HarryPotterListRoute> {
                        HarryPotterListPage(
                            Modifier.padding(innerPadding),
                            onItemClick = { selectedBook: Book ->
                                topLevelBackStack.add(HarryPotterDetailRoute(selectedBook))
                            }
                        )
                    }
                    entry<HarryPotterDetailRoute> { route ->
                        HarryPotterDetailsPage(
                            Modifier.padding(innerPadding),
                            book = route.book,
                        )

                    }

                    entry<AmiiboListRoute> {
                        AmiigoListPage(Modifier.padding(innerPadding))
                    }

                }
            )
        }
    }
}
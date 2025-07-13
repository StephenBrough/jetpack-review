package com.stephenbrough.jetpack_learning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.stephenbrough.jetpack_learning.harry_potter_details_page.HarryPotterDetailsPage
import com.stephenbrough.jetpack_learning.harry_potter_list_page.HarryPotterListPage
import com.stephenbrough.jetpack_learning.landing_page.LandingPage
import com.stephenbrough.jetpack_learning.landing_page.NavDrawerActions
import com.stephenbrough.jetpack_learning.login_page.LoginPage
import com.stephenbrough.jetpack_learning.settings_page.SettingsPage
import com.stephenbrough.jetpack_learning.ui.theme.JetpacklearningTheme
import com.stephenbrough.jetpack_learning.util.navigation.HarryPotterDetail
import com.stephenbrough.jetpack_learning.util.navigation.HarryPotterList
import com.stephenbrough.jetpack_learning.util.navigation.Landing
import com.stephenbrough.jetpack_learning.util.navigation.Loading
import com.stephenbrough.jetpack_learning.util.navigation.Login
import com.stephenbrough.jetpack_learning.util.navigation.Settings
import com.stephenbrough.jetpack_learning.util.navigation.TopLevelBackStack
import com.stephenbrough.jetpack_learning.util.navigation.topLevelSaver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpacklearningTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SharedTransitionLayout {
                        CompositionLocalProvider(LocalNavSharedTransitionScope provides this) {
                            NavigationContainer(innerPadding)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NavigationContainer(innerPadding: PaddingValues) {
        val topLevelBackStack by rememberSaveable(stateSaver = topLevelSaver()) {
            mutableStateOf(TopLevelBackStack<Any>(Loading))
        }
        val authState by authViewModel.authState.collectAsStateWithLifecycle()

        var lastProcessedAuthState by rememberSaveable { mutableStateOf<AuthState?>(null) }

        println("Composing auth state: ${authState::class.simpleName}")

        LaunchedEffect(authState) {
            println("LaunchedEffect triggered with key: ${authState::class.simpleName}")
            println("Last processed: ${lastProcessedAuthState?.let { it::class.simpleName }}")

            // Configuration changes seem to reset the state of the LaunchedEffect, causing it
            // to run every time, regardless of whether the key has changed. This check keeps
            // this LaunchedEffect from running if the key has not changed, regardless of
            // configuration changes
            if (authState == lastProcessedAuthState) return@LaunchedEffect

            lastProcessedAuthState = authState
            when (authState) {
                AuthState.Loading -> {
                    topLevelBackStack.clear(Loading)
                }

                AuthState.LoggedOut -> {
                    topLevelBackStack.clear(Login)
                }

                AuthState.LoggedIn -> {
                    println("Logged in!!!")
                    topLevelBackStack.clear(Landing)
                }

                // TODO: Implement bio authentication
                AuthState.Authenticated -> {}
            }

        }

        NavDisplay(
            backStack = topLevelBackStack.backStack,
            onBack = { topLevelBackStack.removeLast() },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Loading> {
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
                entry<Login> {
                    LoginPage(Modifier.padding(innerPadding), onLoginSuccess = {
                        topLevelBackStack.clear(Landing)
                    })
                }
                entry<Landing> {
                    LandingPage(
                        Modifier.padding(innerPadding),
                        onLogout = { authViewModel.logout() },
                        onNavDrawerAction = {
                            when (it) {
                                NavDrawerActions.SettingsAction -> {
                                    topLevelBackStack.addTopLevel(Settings)
                                }
                            }
                        }
                    )
                }
                entry<Settings> {
                    SettingsPage(Modifier.padding(innerPadding))
                }
                entry<HarryPotterList> {
                    HarryPotterListPage(Modifier.padding(innerPadding))
                }
                entry<HarryPotterDetail> {
                    HarryPotterDetailsPage(Modifier.padding(innerPadding))
                }

            }
        )
    }

    companion object {
        /** The [SharedTransitionScope] of the [NavDisplay]. */
        @OptIn(ExperimentalSharedTransitionApi::class)

        val LocalNavSharedTransitionScope =
            compositionLocalOf<SharedTransitionScope> {
                throw IllegalStateException(
                    "Unexpected access to LocalNavSharedTransitionScope. You must provide a " +
                            "SharedTransitionScope from a call to SharedTransitionLayout() or " +
                            "SharedTransitionScope()"
                )
            }
    }
}
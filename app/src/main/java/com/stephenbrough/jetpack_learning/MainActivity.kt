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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.stephenbrough.jetpack_learning.landing_page.LandingPage
import com.stephenbrough.jetpack_learning.landing_page.NavDrawerActions
import com.stephenbrough.jetpack_learning.login_page.LoginPage
import com.stephenbrough.jetpack_learning.settings_page.SettingsPage
import com.stephenbrough.jetpack_learning.ui.theme.JetpacklearningTheme
import com.stephenbrough.jetpack_learning.util.navigation.LandingRoute
import com.stephenbrough.jetpack_learning.util.navigation.LoadingRoute
import com.stephenbrough.jetpack_learning.util.navigation.LoginRoute
import com.stephenbrough.jetpack_learning.util.navigation.SettingsRoute
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
        val authState by authViewModel.authState.collectAsStateWithLifecycle()

        var lastProcessedAuthState by rememberSaveable { mutableStateOf<AuthState?>(null) }

        val backstack = rememberSaveable { mutableStateListOf<Any>(LoadingRoute) }

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
                    backstack.clear()
                    backstack.add(LoadingRoute)
                }

                AuthState.LoggedOut -> {
                    backstack.clear()
                    backstack.add(LoginRoute)
                }

                AuthState.LoggedIn -> {
                    println("Logged in!!!")
                    backstack.clear()
                    backstack.add(LandingRoute)
                }

                // TODO: Implement bio authentication
                AuthState.Authenticated -> {}
            }

        }

        NavDisplay(
            backStack = backstack,
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
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
                entry<LoginRoute> {
                    LoginPage(Modifier.padding(innerPadding), onLoginSuccess = {
                        backstack.clear()
                        backstack.add(LandingRoute)
                    })
                }
                entry<LandingRoute> {
                    LandingPage(
                        Modifier.padding(innerPadding),
                        onLogout = { authViewModel.logout() },
                        onNavDrawerAction = {
                            when (it) {
                                NavDrawerActions.SettingsAction -> {
                                    backstack.add(SettingsRoute)
                                }
                            }
                        }
                    )
                }
                entry<SettingsRoute> {
                    SettingsPage(Modifier.padding(innerPadding))
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
package com.stephenbrough.jetpack_learning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.stephenbrough.jetpack_learning.landing_page.LandingPage
import com.stephenbrough.jetpack_learning.login.LoginPage
import com.stephenbrough.jetpack_learning.ui.theme.JetpacklearningTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
        val backStack = rememberNavBackStack(LoginPage)

        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
                ),
            entryProvider = entryProvider {
                entry<LoginPage> {
                    LoginPage(Modifier.padding(innerPadding), onLoginSuccess = {
                        backStack.clear()
                        backStack.add(LandingPage)
                    })
                }
                entry<LandingPage> {
                    LandingPage(Modifier.padding(innerPadding))
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

@Serializable
data object LoginPage : NavKey

@Serializable
data object LandingPage : NavKey

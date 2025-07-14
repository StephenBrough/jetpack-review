package com.stephenbrough.jetpack_learning.util.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toys
import androidx.compose.ui.graphics.vector.ImageVector
import com.stephenbrough.jetpack_learning.domain.Amiibo
import com.stephenbrough.jetpack_learning.domain.Book
import com.stephenbrough.jetpack_learning.domain.StarWarsMovie
import kotlinx.parcelize.Parcelize


@Parcelize
sealed interface TopLevelRoute : Parcelable {
    val icon: ImageVector
    val title: String
}

data object LoadingRoute

@Parcelize
data object HarryPotterListRoute : TopLevelRoute {
    override val icon = Icons.Default.Book
    override val title = "Harry Potter"
}

data class HarryPotterDetailRoute(val book: Book)

@Parcelize
data object AmiiboListRoute : TopLevelRoute {
    override val icon = Icons.Default.Toys
    override val title = "Amiibo"
}

@Parcelize
data object StarWarsListRoute : TopLevelRoute {
    override val icon = Icons.Default.Star
    override val title = "Star Wars"
}

data class StarWarsDetailsRoute(val id: String)

data class AmiiboDetailRoute(val amiibo: Amiibo)

val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(
    HarryPotterListRoute, AmiiboListRoute,
    StarWarsListRoute,
)


data object LoginRoute
data object SettingsRoute
data object LandingRoute
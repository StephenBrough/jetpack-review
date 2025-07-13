package com.stephenbrough.jetpack_learning.util.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toys
import androidx.compose.ui.graphics.vector.ImageVector
import com.stephenbrough.jetpack_learning.domain.Book
import kotlinx.parcelize.Parcelize


@Parcelize
sealed interface TopLevelRoute : Parcelable {
    val icon: ImageVector
}

data object LoadingRoute

@Parcelize
data object HarryPotterListRoute : TopLevelRoute {
    override val icon = Icons.Default.Star
}

data class HarryPotterDetailRoute(val book: Book)

@Parcelize
data object AmiiboListRoute : TopLevelRoute {
    override val icon = Icons.Default.Toys
}

data object AmiiboDetailRoute

val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(HarryPotterListRoute, AmiiboListRoute)


data object LoginRoute
data object SettingsRoute
data object LandingRoute
package com.stephenbrough.jetpack_learning.util.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toys
import androidx.compose.ui.graphics.vector.ImageVector


sealed interface TopLevelRoute { val icon: ImageVector }

data object Loading
data object Login
data object Landing
data object Settings
data object HarryPotterList : TopLevelRoute { override val icon = Icons.Default.Star}
data object HarryPotterDetail
data object AmiiboList : TopLevelRoute { override val icon = Icons.Default.Toys}
data object AmiigoDetail

val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(HarryPotterList, AmiiboList)

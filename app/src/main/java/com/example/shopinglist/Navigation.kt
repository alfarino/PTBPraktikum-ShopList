package com.example.shopinglist

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Profile : Screen("profile", "Profile")
    data object Setting : Screen("setting", "Setting")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, "Profile")
)

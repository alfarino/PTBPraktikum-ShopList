package com.example.shopinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.shopinglist.components.AppDrawer
import com.example.shopinglist.components.BottomNavigationBar
import com.example.shopinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Profile : Screen("profile", "Profile")
    data object Setting : Screen("setting", "Setting")
}

data class BottomNavItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Screen.Profile, Icons.Filled.Person, "Profile")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }

    val currentScreen = when (currentRoute) {
        Screen.Profile.route -> Screen.Profile
        Screen.Setting.route -> Screen.Setting
        else -> Screen.Home
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            AppDrawer(
                selectedRoute = currentRoute,
                onDestinationClick = { destination ->
                    currentRoute = destination.route
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = currentScreen.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (drawerState.isOpen) {
                                    Icons.Filled.Settings
                                } else {
                                    Icons.Filled.Menu
                                },
                                contentDescription = "Buka menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    items = bottomNavItems,
                    selectedRoute = currentRoute,
                    onItemSelected = { selected ->
                        currentRoute = selected.screen.route
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentRoute,
                    transitionSpec = {
                        // determine navigation direction by comparing route indices
                        val order = listOf(Screen.Home.route, Screen.Profile.route, Screen.Setting.route)
                        val initialIndex = order.indexOf(initialState)
                        val targetIndex = order.indexOf(targetState)
                        val forward = targetIndex > initialIndex

                        // explicit offsets: when moving forward new screen comes from right and old exits to left
                        // when moving backward new screen comes from left and old exits to right
                        val enter = if (forward) {
                            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> fullWidth / 4 }
                            )
                        } else {
                            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth -> -fullWidth / 4 }
                            )
                        }

                        val exit = if (forward) {
                            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { fullWidth -> -fullWidth / 4 }
                            )
                        } else {
                            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { fullWidth -> fullWidth / 4 }
                            )
                        }

                        enter.togetherWith(exit)
                    },
                    label = "screen_transition"
                ) { route ->
                    when (route) {
                        Screen.Profile.route -> ProfileScreen()
                        Screen.Setting.route -> SettingScreen()
                        else -> HomeScreen()
                    }
                }
            }
        }
    }
}
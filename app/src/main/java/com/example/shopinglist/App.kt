package com.example.shopinglist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.shopinglist.components.AppDrawer
import com.example.shopinglist.components.BottomNavigationBar
import com.example.shopinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainAppContent() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
    var currentDetailItem by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = androidx.compose.material3.SnackbarHostState()
    val shoppingItems = remember { androidx.compose.runtime.mutableStateListOf(
        "Susu Segar",
        "Roti Tawar",
        "Telur Ayam",
        "Apel Fuji",
        "Daging Sapi"
    ) }

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
            floatingActionButton = {
                androidx.compose.material3.FloatingActionButton(onClick = {
                    val newItem = "Item ${shoppingItems.size + 1}"
                    shoppingItems.add(newItem)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Menambahkan: $newItem")
                    }
                }) {
                    androidx.compose.material3.Icon(imageVector = Icons.Filled.Add, contentDescription = "Tambah")
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                AnimatedContent(targetState = currentRoute, transitionSpec = {
                    if (initialState == Screen.Home.route && targetState == "detail") {
                        slideInHorizontally(animationSpec = tween(300)) { full -> full } + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(300)) { full -> -full } + fadeOut()
                    } else if (initialState == "detail" && targetState == Screen.Home.route) {
                        slideInHorizontally(animationSpec = tween(300)) { full -> -full } + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(300)) { full -> full } + fadeOut()
                    } else {
                        fadeIn(animationSpec = tween(200)) with fadeOut(animationSpec = tween(200))
                    }
                }) { route ->
                    when (route) {
                        Screen.Profile.route -> ProfileScreen()
                        Screen.Setting.route -> SettingScreen()
                        "detail" -> {
                            val item = currentDetailItem ?: ""
                            DetailScreen(item = item, onBack = {
                                currentDetailItem = null
                                currentRoute = Screen.Home.route
                            })
                        }
                        else -> HomeScreen(shoppingItems = shoppingItems, onItemClick = { item ->
                            currentDetailItem = item
                            currentRoute = "detail"
                        })
                    }
                }
            }
        }
    }
}

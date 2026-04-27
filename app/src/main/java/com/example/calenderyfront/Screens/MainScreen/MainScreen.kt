package com.example.calenderyfront.Screens.MainScreen

import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.calenderyfront.Screens.MainScreen.Scaffold.HomeBottomBar
import com.example.calenderyfront.Screens.home.HomeScreen

/**
 * Scaffold global con navegación
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            HomeBottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(
                    windowSizeClass = WindowSizeClass.calculateFromSize(
                        DpSize(360.dp, 800.dp)
                    )
                )
            }

            composable("profile") {
                // TODO: ProfileScreen()
            }

            composable("search") {
                // TODO: SearchScreen()
            }
        }
    }
}

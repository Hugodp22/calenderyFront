package com.example.calenderyfront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calenderyfront.Screens.LoginScreen
import com.example.calenderyfront.Screens.RegisterScreen
import com.example.calenderyfront.Screens.SettingScreen
import com.example.calenderyfront.ui.theme.CalenderyFrontTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalenderyFrontTheme {
                val windowSize = calculateWindowSizeClass(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Aqui detectariamos si el movil tiene token para saber a que pantalla
                    //Mandar o no al iniciar la app
                    //if token bla bla bla

                    CalenderyApp(
                        modifier = Modifier.padding(innerPadding),
                        windowSize = windowSize.widthSizeClass
                    )
                }
            }
        }
    }
}

@Composable
fun CalenderyApp(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
)
{
    //Controlador que empieza en la pantalla de register. Falta poner
    //Comprobacion de si tiene token o no a implementar en el futuro.
    NavHost(navController = navController, startDestination = "register") {
        composable(route = "register") {
            RegisterScreen(
                modifier = Modifier,
                onNavigateToSettings = { userId ->
                    navController.navigate("settings/$userId")
                                       },
                onNavigateToLogin = {
                    navController.navigate("login")
                                    },
                windowSize = windowSize,
            )
        }

        composable(route = "login") {
            LoginScreen(
                modifier = Modifier,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                //Haria falta un onNavigateToMain aqui y obvio hacer la peticion y el model ahi
                windowSize = windowSize,
            )
        }

        composable(
            route = "settings/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        )
        {
            SettingScreen(
                    modifier = Modifier,
                    windowSize = windowSize,
                )
        }
    }
}

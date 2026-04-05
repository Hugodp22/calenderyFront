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
                    SettingScreen(
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
    navController: NavHostController = rememberNavController(),
    windowSize: WindowWidthSizeClass,
)
{
    //Controlador que empieza en la pantalla de register. Falta poner
    //Comprobacion de si tiene token o no a implementar en el futuro.

    NavHost(navController = navController, startDestination = "register") {

        composable("register") {
            RegisterScreen(
                modifier = Modifier,
                onNavigateToSettings = { userId ->
                    navController.navigate("settings/$userId")
                },
                windowSize = windowSize,
            )
        }

        composable("settings/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") //Obtenemos el id
            //SettingScreen(userId = userId) y seria cargar el view model con los datos de este usuario
        }
    }
}

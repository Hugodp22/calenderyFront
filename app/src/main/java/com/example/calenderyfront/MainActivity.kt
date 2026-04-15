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
import com.example.calenderyfront.Model.DataObjects.Login
import com.example.calenderyfront.Model.DataObjects.Register
import com.example.calenderyfront.Model.DataObjects.Settings
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.Screens.LoginScreen
import com.example.calenderyfront.Screens.RegisterScreen
import com.example.calenderyfront.Screens.SettingScreen
import com.example.calenderyfront.Screens.WaitingForLinkScreen
import com.example.calenderyfront.ui.theme.CalenderyFrontTheme
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext) //Se inicia UNA SOLA VEZ
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
    val uri = "https://calenderyback.com"

    //Controlador que empieza en la pantalla de register. Falta poner
    //Comprobacion de si tiene token o no a implementar en el futuro.

    NavHost(navController = navController, startDestination = Register) {
        composable<Register> {
            RegisterScreen(
                modifier = Modifier,
                onNavigateToWaiting = { userInfo ->
                    navController.navigate(VerifyLink(userInfo))
                                       },
                onNavigateToLogin = {
                    navController.navigate(Login)
                                    },
                windowSize = windowSize,
            )
        }

        composable<VerifyLink>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
            //deepLinks = listOf(navDeepLink<VerifyLink>(basePath = "$uri/registrationConfirm"))
        )
        { path ->
            //val token = verifyLink.token

            //if (token == null) {
                WaitingForLinkScreen(
                    modifier = Modifier,
                    onNavigateToSettings = { userInfo ->
                        navController.navigate(Settings(userInfo))
                    },
                    windowSize = windowSize
                )
            //}

            //else {
            //    WaitingToSendTokenScreen(
            //        modifier = Modifier,
            //        onNavigateToSettings = { userInfo ->
            //            navController.navigate(Settings(userInfo))
            //        },
            //        token = token,
            //        windowSize = windowSize
            //    )
            //}
        }

        composable<Login> {
            LoginScreen(
                modifier = Modifier,
                onNavigateToRegister = {
                    navController.navigate(Register)
                },
                //Haria falta un onNavigateToMain aqui y obvio hacer la peticion y el model ahi
                windowSize = windowSize,
            )
        }

        composable<Settings>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            SettingScreen(
                    modifier = Modifier,
                    windowSize = windowSize,
                )
        }
    }
}

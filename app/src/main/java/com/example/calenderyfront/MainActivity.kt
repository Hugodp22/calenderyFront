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
import com.example.calenderyfront.Model.dataObjects.Login
import com.example.calenderyfront.Model.dataObjects.Profile
import com.example.calenderyfront.Model.dataObjects.Redirect
import com.example.calenderyfront.Model.dataObjects.Register
import com.example.calenderyfront.Model.dataObjects.Home
import com.example.calenderyfront.Model.dataObjects.Settings
import com.example.calenderyfront.Model.dataObjects.Upload
import com.example.calenderyfront.Model.dataObjects.UserInfo
import com.example.calenderyfront.Model.dataObjects.UserInfoNavType
import com.example.calenderyfront.Model.dataObjects.VerifyLink
import com.example.calenderyfront.Model.dataObjects.PostDataUpload
import com.example.calenderyfront.screens.LoginScreen
import com.example.calenderyfront.screens.PostDataUploadScreen
import com.example.calenderyfront.screens.ProfileScreen
import com.example.calenderyfront.screens.RedirectScreen
import com.example.calenderyfront.screens.RegisterScreen
import com.example.calenderyfront.screens.SettingScreen
import com.example.calenderyfront.screens.UploadScreen
import com.example.calenderyfront.screens.WaitingForLinkScreen
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.screens.HomeScreen
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
    NavHost(navController = navController, startDestination = Redirect) {

        composable<Redirect> {
            RedirectScreen(
                modifier = Modifier,
                onNavigateToLogin = {
                    navController.navigate(Login)
                },
                onNavigateToWaitingForLink = { userInfo ->
                    navController.navigate(VerifyLink(userInfo))
                },
                onNavigateToProfile = { userInfo ->
                    navController.navigate(Home(userInfo))
                }
            )
        }

        composable<Register> {
            RegisterScreen(
                modifier = Modifier,
                onNavigateToWaiting = { userInfo ->
                    navController.navigate(VerifyLink(userInfo)) {
                        //Borramos del historial del navController hasta startDestination
                        //que seria el redirect, para asi evitar errores al poder ir hacia atras
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true //Evitamos hacer copias de pantallas.
                    }
                                      },
                onNavigateToLogin = {
                    navController.navigate(Login)
                                    },
                windowSize = windowSize,
            )
        }

        composable<VerifyLink>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            WaitingForLinkScreen(
            modifier = Modifier,
            onNavigateToSettings = { userInfo ->
                navController.navigate(Settings(userInfo)) },
            windowSize = windowSize
        )
        }

        composable<Login> {
            LoginScreen(
                modifier = Modifier,
                onNavigateToRegister = {
                    navController.navigate(Register)
                },
                onNavigateToProfile = { userInfo ->
                    navController.navigate(Profile(userInfo))
                },
                windowSize = windowSize,
            )
        }

        composable<Settings>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            SettingScreen(
                modifier = Modifier,
                onNavigateToProfile = { userInfo ->
                    navController.navigate((Profile(userInfo)))
                },
                    windowSize = windowSize,
                )
        }

        composable<Profile>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            ProfileScreen(
                modifier = Modifier,
                windowSize = windowSize,
                onNavigateToSettings = { userInfo ->
                    navController.navigate(Settings(userInfo))
                },
                onNavigateToUpload = { userInfo ->
                    navController.navigate(Upload(userInfo))
                }
            )
        }

        composable<Upload>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            UploadScreen(
                modifier = Modifier,
                windowSize = windowSize,
                onNavigateToUpload = { userInfo, postId ,photoPath, photoUrl ->
                    navController.navigate(PostDataUpload(userInfo,postId, photoPath, photoUrl))
                }
            )
        }

        composable<PostDataUpload>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            PostDataUploadScreen(
                modifier = Modifier,
                windowSize = windowSize,
                onNavigateToProfile = { userInfo ->
                    navController.navigate(Profile(userInfo))
                }
            )
        }

        composable<Home>(
            typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
        )
        {
            HomeScreen(
                modifier = Modifier,
                windowSize = windowSize,
                onNavigateToOtherProfile = { userInfo, otherUserId ->

                }
            )
        }
    }
}

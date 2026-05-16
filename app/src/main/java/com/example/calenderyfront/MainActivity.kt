package com.example.calenderyfront

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Chat
import com.example.calenderyfront.Model.DataObjects.Home
import com.example.calenderyfront.Model.DataObjects.Login
import com.example.calenderyfront.Model.DataObjects.MessageResponseDto
import com.example.calenderyfront.Model.DataObjects.PostDataUpload
import com.example.calenderyfront.Model.DataObjects.Profile
import com.example.calenderyfront.Model.DataObjects.Redirect
import com.example.calenderyfront.Model.DataObjects.Register
import com.example.calenderyfront.Model.DataObjects.Selection
import com.example.calenderyfront.Model.DataObjects.Settings
import com.example.calenderyfront.Model.DataObjects.Upload
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.Screens.ChatScreen
import com.example.calenderyfront.Screens.HomeScreen
import com.example.calenderyfront.Screens.LoginScreen
import com.example.calenderyfront.Screens.PostDataUploadScreen
import com.example.calenderyfront.Screens.ProfileScreen
import com.example.calenderyfront.Screens.RedirectScreen
import com.example.calenderyfront.Screens.RegisterScreen
import com.example.calenderyfront.Screens.SelectionScreen
import com.example.calenderyfront.Screens.SettingScreen
import com.example.calenderyfront.Screens.UploadScreen
import com.example.calenderyfront.Screens.WaitingForLinkScreen
import com.example.calenderyfront.bottomBar.BottomBarState
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.clients.WebSocketClient
import com.example.calenderyfront.observer.AppLifecycleObserver
import com.example.calenderyfront.service.WebSocketService
import com.example.calenderyfront.ui.theme.BebasNeue
import com.example.calenderyfront.ui.theme.CalenderyFrontTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cargamos el servicio de webSocket
        val intent = Intent(this, WebSocketService::class.java)
        startService(intent)
        //Añadimos un observador a la app
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
        RetrofitClient.init(applicationContext) //Iniciamos el retroFit para las peticiones
        enableEdgeToEdge()
        setContent {
            CalenderyFrontTheme {
                val windowSize = calculateWindowSizeClass(this)
                    CalenderyApp(
                        modifier = Modifier.padding(),
                        windowSize = windowSize.widthSizeClass
                    )
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var bottomBarState by remember { mutableStateOf(BottomBarState(currentScreen = currentDestination)) }
    val userInChatSelection = bottomBarState.currentScreen?.hasRoute<Selection>() ?: false

    LaunchedEffect(Unit) {
        WebSocketClient.messageFlow.collect { messageJSON ->
            if (!userInChatSelection) {
                bottomBarState = bottomBarState.copy(newMessage = true)
                val messageResponseDtoReceived = Gson().fromJson(messageJSON, MessageResponseDto::class.java)
                launch {
                    RetrofitClient.chatApi.marcarNuevoMensajeComoPendiente(idMensaje = messageResponseDtoReceived.idMensaje)
                }
            }
        }
    }

    LaunchedEffect(userInChatSelection) {
        if (userInChatSelection) {
            bottomBarState = bottomBarState.copy(newMessage = false)
        }
    }

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<Home>() || dest.hasRoute<Selection>() || dest.hasRoute<Profile>()
    } ?: false

    val showTopBar = currentDestination?.let { dest ->
        dest.hasRoute<Home>() || dest.hasRoute<Selection>()
    } ?: false

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                CalenderyBottomBar(
                    navController = navController,
                    navBackStackEntry = navBackStackEntry,
                    bottomBarState = bottomBarState,
                    windowSize = windowSize
                )
            }
        },
        topBar = {
            if (showTopBar) {
                CalenderyTopBar(
                    windowSize = windowSize,
                )
            }
        }
    )
    { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Redirect,
        )
        {
            composable<Redirect> {
                RedirectScreen(
                    modifier = Modifier,
                    onNavigateToLogin = {
                        navController.navigate(Login)
                    },
                    onNavigateToWaitingForLink = { userInfo ->
                        navController.navigate(VerifyLink(userInfo))
                    },
                    onNavigateToHome = { userInfo ->
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
                        navController.navigate(Settings(userInfo))
                    },
                    windowSize = windowSize
                )
            }

            composable<Login> {
                LoginScreen(
                    modifier = Modifier,
                    onNavigateToRegister = {
                        navController.navigate(Register)
                    },
                    onNavigateToHome = { userInfo ->
                        navController.navigate(Home(userInfo))
                    },
                    windowSize = windowSize,
                )
            }

            composable<Selection>(
                typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
            )
            {
                SelectionScreen(
                    modifier = Modifier,
                    windowSize = windowSize,
                    onNavigateToOtherProfile = {userInfo,otherUserId ->
                        navController.navigate(Profile(userInfo,otherUserId))
                    },
                    onNavigateToChat = {userInfo,otherUserId,chatId, otherUserName, otherUserPhoto ->
                        navController.navigate(Chat(userInfo,otherUserId,chatId, otherUserName, otherUserPhoto))
                    }
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
                    onNavigateToLogin =  {
                        navController.navigate(Login)
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
                    },
                    onNavigateToChat = { userInfo, otherUserId,chatId, otherUserName, otherUserPhoto ->
                        navController.navigate(Chat(userInfo,otherUserId,chatId,otherUserName,otherUserPhoto))
                    },
                    onNavigateToOtherProfile = { userInfo, otherUserId ->
                        navController.navigate(Profile(userInfo, otherUserId))
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
                    onNavigateToUpload = { userInfo, postId, photoPath, photoUrl ->
                        navController.navigate(
                            PostDataUpload(
                                userInfo = userInfo,
                                postId = postId,
                                photoPath = photoPath,
                                photoUrl = photoUrl
                            )
                        )
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
                        navController.navigate(Profile(userInfo, otherUserId))
                    }
                )
            }

            composable<Chat>(
                typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
            )
            {
                ChatScreen(
                    windowSize = windowSize,
                    onNavigateToOtherProfile = {userInfo, otherUserId ->
                        navController.navigate(Profile(userInfo,otherUserId))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderyTopBar(
    windowSize: WindowWidthSizeClass
)
{
    val logoSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 72.dp
        WindowWidthSizeClass.Medium -> 86.dp
        WindowWidthSizeClass.Expanded -> 96.dp
        else -> 72.dp
    }

    val titleSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 28.sp
        WindowWidthSizeClass.Medium -> 34.sp
        WindowWidthSizeClass.Expanded -> 38.sp
        else -> 28.sp
    }

    val spacerWidth = when (windowSize) {
        WindowWidthSizeClass.Compact -> 8.dp
        WindowWidthSizeClass.Medium -> 10.dp
        WindowWidthSizeClass.Expanded -> 12.dp
        else -> 8.dp
    }

    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.calendary_app),
                    modifier = Modifier.size(logoSize)
                )

                Spacer(
                    modifier = Modifier.width(spacerWidth)
                )

                Text(
                    text = stringResource(R.string.calendary_app),
                    fontFamily = BebasNeue,
                    fontSize = titleSize,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    )
}

@Composable
fun CalenderyBottomBar(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController,
    bottomBarState: BottomBarState,
    navBackStackEntry: NavBackStackEntry?
)
{
    val currentDestination = navBackStackEntry?.destination
    val currentUserInfo = navBackStackEntry?.toRoute<Profile>()?.userInfo //Obtenemos el userInfo de la pantalla actual

    if (currentUserInfo != null) {

        val iconSize = when (windowSize) {
            WindowWidthSizeClass.Compact -> 25.dp
            else -> 25.dp
        }

        val navigationBarHeight = when (windowSize) {
            WindowWidthSizeClass.Compact -> 65.dp
            else -> 65.dp
        }

        val iconColor =
            if (bottomBarState.newMessage) MaterialTheme.colorScheme.onPrimaryFixed
            else Color.Unspecified

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.navigationBarsPadding().height(navigationBarHeight)
        )
        {

            NavigationBarItemCalendery(
                selected = currentDestination == Home,
                onClick = {navController.navigate(Home(
                    userInfo = currentUserInfo)
                )},
                icon = R.drawable.home,
                contentDescription = R.string.Home_bottom_bar,
                iconSize = iconSize
            )

            NavigationBarItemCalendery(
                selected = currentDestination == Selection,
                onClick = {navController.navigate(Selection(
                    userInfo = currentUserInfo,
                    chatOption = false)
                )},
                icon = R.drawable.search,
                contentDescription = R.string.Search_bottom_bar,
                iconSize = iconSize
            )

            NavigationBarItemCalendery(
                selected = currentDestination == Selection,
                onClick = {navController.navigate(Selection(
                    userInfo = currentUserInfo,
                    chatOption = true)
                )},
                icon = R.drawable.chat,
                contentDescription = R.string.Chat_bottom_bar,
                iconSize = iconSize,
                color = iconColor
            )

            NavigationBarItemCalendery(
                selected = currentDestination == Profile,
                onClick = {navController.navigate(Profile(
                    userInfo = currentUserInfo)
                )},
                icon = R.drawable.profile,
                contentDescription = R.string.Profile_bottom_bar,
                iconSize = iconSize
            )
        }
    }
}

@Composable
fun RowScope.NavigationBarItemCalendery( //Añadimos RowScope para usar la funcion NavigationBarItem
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    iconSize: Dp,
    color: Color = Color.Unspecified
)
{
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = stringResource(contentDescription),
                modifier = Modifier.size(iconSize),
                tint = color
            )
        },
        alwaysShowLabel = false
    )
}

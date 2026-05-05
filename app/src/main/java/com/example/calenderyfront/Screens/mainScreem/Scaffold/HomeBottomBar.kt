package com.example.calenderyfront.Screens.mainScreem.Scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.calenderyfront.R


//Mover a MainActivity, pasarle funciones en vez del nav, investigar como coño pasarle el UserInfo
//Hacer tamaño dinamico tanto aqui como en el HomeScreen, cambiar nombres de imagenes de drawable

//PARA CADA PUTA COSA QUE HAGAS Y ACABES, HACES PR Y UPDATEAS EL PUTO MAIN

@Composable
fun HomeBottomBar(navController: NavController) {

    val currentRoute by navController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.navigationBarsPadding()
    )
    {
        NavigationBarItem(
            selected = currentRoute?.destination?.route == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home")
                    launchSingleTop = true
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (currentRoute?.destination?.route == "home")
                                MaterialTheme.colorScheme.onPrimary
                            else Color.Transparent
                        )
                        .padding(6.dp)
                )
                {
                    Icon(
                        painter = painterResource(R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentRoute?.destination?.route == "search",
            onClick = { navController.navigate("search") },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(30.dp)
                )
            },
            alwaysShowLabel = false
        )


        NavigationBarItem(
            selected = currentRoute?.destination?.route == "calendar",
            onClick = { navController.navigate("calendar") },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.calendario_oscuro),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(30.dp)
                )
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentRoute?.destination?.route == "profile",
            onClick = { navController.navigate("profile") },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(30.dp)
                )
            },
            alwaysShowLabel = false
        )
    }
}
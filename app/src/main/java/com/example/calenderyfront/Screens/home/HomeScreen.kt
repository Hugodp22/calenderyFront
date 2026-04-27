package com.example.calenderyfront.Screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.RetrofitClient
import com.example.calenderyfront.Screens.MainScreen.Scaffold.HomeBottomBar
import com.example.calenderyfront.Screens.home.Components.HomeTopBar
import com.example.calenderyfront.Screens.home.Components.PostItem


@Composable
fun HomeScreen(
    windowSizeClass: WindowSizeClass
) {

    /**
     * Inicialización manual del ViewModel
     */
    val viewModel: HomeViewModel = remember {
        HomeViewModel(
            PublicacionRepository(
                RetrofitClient.publicacionApi
            )
        )
    }

    /**
     * Observamos estado de la UI
     */
    val uiState by viewModel.uiState.collectAsState()

    /**
     * Estructura base de la pantalla
     */
    Scaffold(
        topBar = { HomeTopBar() }
    ) { padding ->

        HomeContent(
            posts = uiState.posts,
            modifier = Modifier.padding(padding),
            windowSizeClass = windowSizeClass
        )

        when {

            /**
             * Estado de carga
             */
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            /**
             * Estado de error
             */
            uiState.error != null -> {
                Text(uiState.error!!)
            }

            /**
             * Contenido principal
             */
            else -> {
                HomeContent(
                    posts = uiState.posts,
                    modifier = Modifier.padding(padding),
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    posts: List<PublicacionHome>,
    modifier: Modifier,
    windowSizeClass: WindowSizeClass
) {

    val padding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 12.dp
        WindowWidthSizeClass.Medium -> 32.dp
        else -> 64.dp
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(padding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(posts) {
            PostItem(it)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {

    val navController = rememberNavController()

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar(navController) }
    ) { padding ->

        HomeContent(
            posts = emptyList(),
            modifier = Modifier.padding(padding),
            windowSizeClass = WindowSizeClass.calculateFromSize(
                DpSize(360.dp, 800.dp)
            )
        )
    }
}
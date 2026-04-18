package com.example.calenderyfront.Screens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.profile.ProfileState
import com.example.calenderyfront.profile.ProfileViewModel
import com.example.calenderyfront.ui.theme.CalenderyFrontTheme

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    userName: String,
    photoUser: String,
    description: String?,
    numberOfFollowers: Int = 0,
    numberOfFollowed: Int = 0,
)
{
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 20.dp
        WindowWidthSizeClass.Expanded -> 22.dp
        else -> 24.dp
    }

    val photoSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 100.dp
        WindowWidthSizeClass.Medium -> 140.dp
        WindowWidthSizeClass.Expanded -> 150.dp
        else -> 100.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 20.sp
        WindowWidthSizeClass.Expanded -> 22.sp
        else -> 15.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = width, vertical = 16.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            PhotoUserContainer(Modifier.size(photoSize),photoUser,{}, R.string.User_profile_foto)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ProfileStat(windowSize,numberOfFollowers,R.string.followers_text)
                ProfileStat(windowSize,numberOfFollowed,R.string.followed_text)
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(
            Modifier.fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onPrimary)
        )
        {
            Text(
                text = userName,
                color = MaterialTheme.colorScheme.tertiary
            )

            if (!description.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = description,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun ProfileStat(
    windowSize: WindowWidthSizeClass,
    number: Int,
    @StringRes label: Int
)
{
    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 16.sp
        WindowWidthSizeClass.Medium -> 20.sp
        WindowWidthSizeClass.Expanded -> 22.sp
        else -> 16.sp
    }
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number.toString(),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = stringResource(label),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToSettings: (UserInfo) -> Unit,
    viewModel: ProfileViewModel = viewModel(),
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val gridState = rememberLazyGridState() //State para obtener informacion del lazy

    //Para detectar cuando el scroll esta en el final
    val scrollEnElFinal by remember {
        //Se actualiza cada vez que el usuario haga scroll
        derivedStateOf {

            //Obtenemos del lazy que tenga el gridState, la cantidad total de elementos
            val totalItems = gridState.layoutInfo.totalItemsCount

            //Obtenemos de todos los items que se han cargado, el indice del ultimo
            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            //Si el ultimo elemento visible esta a 3 posiciones (1 fila) o menos del final, devuelve true
            //para pedir mas fotos
            totalItems > 0 && ultimoItem >= (totalItems - 3)
        }
    }

    //Si estamos en el final, no esta cargando, y aun no es la ultima pagina, cargamos publicaciones
    LaunchedEffect(scrollEnElFinal) {
        if (scrollEnElFinal && stateProcess != ProfileState.Cargando && !uiState.ultimaPagina) {
            viewModel.loadPublications()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), //Columnas de 3 en 3 publicaciones. Para el tamaño y eso
        state = gridState,
        modifier = modifier.fillMaxSize()
    )
    {
        //Aqui cargamos la cabecera

        items(uiState.publicaciones) { publicacion ->
            //Aqui generamos cada publicacione
        }
    }
    if (stateProcess is ProfileState.Cargando) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Preview(showBackground = true, name = "Phone - Compact")
@Composable
fun PreviewProfileHeaderCompact() {
    CalenderyFrontTheme { // Reemplaza con el nombre de tu tema
        ProfileHeader(
            windowSize = WindowWidthSizeClass.Compact,
            userName = "Alex Designer",
            photoUser = "https://example.com/photo.jpg",
            description = "Entusiasta del UI/UX y amante del café. ☕️ Diseñando el futuro bit a bit.",
            numberOfFollowers = 1250,
            numberOfFollowed = 450
        )
    }
}

@Preview(showBackground = true, name = "Phone - Compact (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewProfileHeaderCompactDark() {
    CalenderyFrontTheme {
        ProfileHeader(
            windowSize = WindowWidthSizeClass.Compact,
            userName = "Alex Designer",
            photoUser = "https://example.com/photo.jpg",
            description = "Entusiasta del UI/UX y amante del café. ☕️ Diseñando el futuro bit a bit.",
            numberOfFollowers = 1250,
            numberOfFollowed = 450
        )
    }
}

@Preview(showBackground = true, widthDp = 700, name = "Tablet - Medium")
@Composable
fun PreviewProfileHeaderMedium() {
    CalenderyFrontTheme() {
        ProfileHeader(
            windowSize = WindowWidthSizeClass.Medium,
            userName = "Alex Designer",
            photoUser = "https://example.com/photo.jpg",
            description = "Entusiasta del UI/UX y amante del café. ☕️",
            numberOfFollowers = 1250,
            numberOfFollowed = 450
        )
    }
}

@Preview(showBackground = true, widthDp = 900, name = "Desktop - Expanded")
@Composable
fun PreviewProfileHeaderExpanded() {
    CalenderyFrontTheme {
        ProfileHeader(
            windowSize = WindowWidthSizeClass.Expanded,
            userName = "Alex Designer",
            photoUser = "https://example.com/photo.jpg",
            description = "Bio extendida para pantallas grandes donde tenemos mucho más espacio para lucir el texto de perfil del usuario.",
            numberOfFollowers = 9900,
            numberOfFollowed = 120
        )
    }
}



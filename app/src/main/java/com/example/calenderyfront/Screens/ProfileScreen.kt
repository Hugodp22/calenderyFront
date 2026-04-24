package com.example.calenderyfront.Screens

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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.ExpandedPhotoProfile
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.profile.ProfileState
import com.example.calenderyfront.profile.ProfileViewModel
import com.example.calenderyfront.rowProfilePostSize



@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    userName: String,
    photoUser: String,
    onClickPhoto: () -> Unit,
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

    val fontSizeName = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        else -> 22.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = width)
            .padding(top = 64.dp)
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            PhotoUserContainer(Modifier.size(photoSize),"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODGxCR4m6d9QvekaDYQFiYEVMYvwS6u7QDw&s",{onClickPhoto()}, R.string.User_profile_foto)
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
                fontWeight = FontWeight.Bold,
                fontSize = fontSizeName,
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
        Spacer(Modifier.padding(bottom = 16.dp))
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

    var expandedPhotoProfile by remember { mutableStateOf(false) } //Para saber cuando la foto esta ampliada

    var selectedPost by remember { mutableStateOf<PublicacionProfile?>(null) } //Para saber que publicacion mostrar

    //Para detectar cuando el scroll esta en el final
    val scrollEnElFinal by remember {
        //Se actualiza cada vez que el usuario haga scroll
        derivedStateOf {

            //Obtenemos del lazy que tenga el gridState, la cantidad total de elementos
            val totalItems = gridState.layoutInfo.totalItemsCount

            //Obtenemos de todos los items que se han cargado, el indice del ultimo
            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            //Si el ultimo elemento visible esta a 1 fila del final, da true
            totalItems > 0 && ultimoItem >= (totalItems - rowProfilePostSize)
        }
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is ProfileState.Exito) {
            onNavigateToSettings((stateProcess as ProfileState.Exito).userInfo) //Cambiar obvio a otra cosa
        }
    }

    //Si estamos en el final, no esta cargando, y aun no es la ultima pagina, cargamos publicaciones
    //LaunchedEffect(scrollEnElFinal) {
    //    if (scrollEnElFinal && stateProcess != ProfileState.Cargando && !uiState.ultimaPagina) {
    //        viewModel.loadPublications()
    //    }
    //}

    LazyVerticalGrid(
        columns = GridCells.Fixed(rowProfilePostSize), //Columnas de 3 en 3 publicaciones. Para el tamaño y eso
        state = gridState,
        modifier = modifier.fillMaxSize()
    )
    {
        //Ponemos la cabecera
        item(span = { GridItemSpan(maxLineSpan) }) {
            ProfileHeader(
                modifier = Modifier,
                windowSize = windowSize,
                userName = uiState.nombreUsuario,
                photoUser = uiState.fotoUsuario,
                onClickPhoto = { expandedPhotoProfile = true },
                description = uiState.descripcion,
                numberOfFollowers = uiState.cantidadSeguidores,
                numberOfFollowed = uiState.cantidadSeguidos
            )
        }

        items(uiState.publicaciones) { publicacion ->
            //Aqui generamos cada publicacione, pasarle a cada una que cuando le des onclick
            //se cambie el mutableState a esa publicacion y asi lo mostramos con un Post
        }
    }

    //Si le has dado click a la foto de perfil, se amplia
    if (expandedPhotoProfile) {
        ExpandedPhotoProfile(
            photoPath = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQODGxCR4m6d9QvekaDYQFiYEVMYvwS6u7QDw&s",
            onDismiss = { expandedPhotoProfile = false }
        )
    }

    if (stateProcess is ProfileState.Cargando) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
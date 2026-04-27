package com.example.calenderyfront.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.example.calenderyfront.Model.dataObjects.PublicacionHome
import com.example.calenderyfront.Model.dataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.rowProfilePostSize
import com.example.calenderyfront.screens.home.HomeState
import com.example.calenderyfront.screens.home.HomeViewModel

val listaPublicaciones = listOf(
    PublicacionHome(
        idUsuario = 1,
        nombreUsuario = "marcos_dev",
        fotoUsuario = "https://randomuser.me/api/portraits/men/1.jpg",
        fotoPublicacion = "https://picsum.photos/id/10/800/600",
        mensaje = "Disfrutando de las vistas en la montaña 🏔️ #senderismo",
        cantidadComentarios = 12,
        cantidadLikes = 150
    ),
    PublicacionHome(
        idUsuario = 2,
        nombreUsuario = "ana.tech",
        fotoUsuario = "https://randomuser.me/api/portraits/women/2.jpg",
        fotoPublicacion = "https://picsum.photos/id/20/800/600",
        mensaje = "Mi nuevo setup de escritorio finalmente terminado. ¿Qué les parece?",
        cantidadComentarios = 45,
        cantidadLikes = 890
    ),
    PublicacionHome(
        idUsuario = 3,
        nombreUsuario = "foodie_traveler",
        fotoUsuario = "https://randomuser.me/api/portraits/men/3.jpg",
        fotoPublicacion = "https://picsum.photos/id/42/800/600",
        mensaje = "La mejor pizza que he probado en toda Italia 🍕🇮🇹",
        cantidadComentarios = 8,
        cantidadLikes = 230
    ),
    PublicacionHome(
        idUsuario = 4,
        nombreUsuario = "clara_reads",
        fotoUsuario = "https://randomuser.me/api/portraits/women/4.jpg",
        fotoPublicacion = null, // Publicación solo de texto
        mensaje = "Acabo de terminar 'Cien años de soledad'. Mi mente ha volado. Recomienden más libros así.",
        cantidadComentarios = 32,
        cantidadLikes = 115
    ),
    PublicacionHome(
        idUsuario = 5,
        nombreUsuario = "pixel_art",
        fotoUsuario = "https://randomuser.me/api/portraits/men/5.jpg",
        fotoPublicacion = "https://picsum.photos/id/60/800/600",
        mensaje = "Probando nuevas técnicas de iluminación en renderizado.",
        cantidadComentarios = 5,
        cantidadLikes = 78
    ),
    PublicacionHome(
        idUsuario = 6,
        nombreUsuario = "fitness_journey",
        fotoUsuario = "https://randomuser.me/api/portraits/women/6.jpg",
        fotoPublicacion = "https://picsum.photos/id/103/800/600",
        mensaje = "Día 30 del reto completado. ¡No te rindas! 💪",
        cantidadComentarios = 21,
        cantidadLikes = 440
    ),
    PublicacionHome(
        idUsuario = 7,
        nombreUsuario = "urban_explorer",
        fotoUsuario = "https://randomuser.me/api/portraits/men/7.jpg",
        fotoPublicacion = "https://picsum.photos/id/122/800/600",
        mensaje = "Calles escondidas de Madrid.",
        cantidadComentarios = 14,
        cantidadLikes = 312
    ),
    PublicacionHome(
        idUsuario = 8,
        nombreUsuario = "code_master",
        fotoUsuario = "https://randomuser.me/api/portraits/men/8.jpg",
        fotoPublicacion = "https://picsum.photos/id/160/800/600",
        mensaje = "Kotlin es, sin duda, mi lenguaje favorito para Android. 🤖",
        cantidadComentarios = 56,
        cantidadLikes = 1200
    ),
    PublicacionHome(
        idUsuario = 9,
        nombreUsuario = "minimal_vibes",
        fotoUsuario = "https://randomuser.me/api/portraits/women/9.jpg",
        fotoPublicacion = "https://picsum.photos/id/201/800/600",
        mensaje = null, // Solo imagen, sin mensaje
        cantidadComentarios = 3,
        cantidadLikes = 95
    ),
    PublicacionHome(
        idUsuario = 10,
        nombreUsuario = "nature_lover",
        fotoUsuario = "https://randomuser.me/api/portraits/women/10.jpg",
        fotoPublicacion = "https://picsum.photos/id/237/800/600",
        mensaje = "Miren este perrito que me encontré en el parque 🐶✨",
        cantidadComentarios = 89,
        cantidadLikes = 2500
    )
)

@Composable
fun PostHomeCreation(
    userInfo: UserInfo,
    post: PublicacionHome,
    windowSize: WindowWidthSizeClass,
    onClickPostPhoto: () -> Unit,
    onClickPostProfile: (UserInfo, Int) -> Unit,
    onClickLikeIcon: () -> Unit,
    onClickCommentIcon: () -> Unit
)
{
    val photoSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 30.dp
        WindowWidthSizeClass.Expanded -> 33.dp
        else -> 20.dp
    }

    val fontSizeUser = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 15.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    val fontSizeMessage = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 15.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        WindowWidthSizeClass.Medium -> 0.8F
        WindowWidthSizeClass.Expanded -> 0.8F
        else -> 0.8F
    }

    Card(
        modifier = Modifier.fillMaxWidth(width)
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(0.5F),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            )
            {
                PhotoUserContainer(modifier = Modifier.size(photoSize),post.fotoPublicacion,{onClickPostProfile(userInfo, post.idUsuario)}, R.string.post_description)
                Text(
                    text = post.nombreUsuario,
                    fontSize = fontSizeUser,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            PostHome(Modifier,post.fotoPublicacion,windowSize,{onClickPostPhoto()})

            if (post.mensaje != null) {
                Text(
                    text = post.mensaje,
                    fontSize = fontSizeMessage,
                    color = MaterialTheme.colorScheme.tertiary,
                    softWrap = true
                )
            }

            RowIcons(
                modifier = Modifier.fillMaxWidth(0.7F),
                windowSize = windowSize,
                onClickLikeIcon ={onClickLikeIcon()},
                onClickCommentIcon = {onClickCommentIcon()},
                likes = post.cantidadLikes,
                currentComments = post.cantidadComentarios
            )
        }
    }
}

@Composable
fun PostHome(
    modifier: Modifier = Modifier,
    photoPath: String?,
    windowSize: WindowWidthSizeClass,
    onClickPostPhoto: () -> Unit
)
{
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.6F
        WindowWidthSizeClass.Medium -> 0.6F
        WindowWidthSizeClass.Expanded -> 0.6F
        else -> 0.6F
    }

    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.6F
        WindowWidthSizeClass.Medium -> 0.6F
        WindowWidthSizeClass.Expanded -> 0.6F
        else -> 0.6F
    }

    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(photoPath).crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(photoPath.toString().substringBefore("?"))
            .precision(Precision.INEXACT)
            .build(),
        contentDescription = stringResource(R.string.post_description),
        modifier = modifier
            .fillMaxWidth(width)
            .fillMaxHeight(height)
            .clickable { onClickPostPhoto() }
            .background(MaterialTheme.colorScheme.primary),
        placeholder = painterResource(R.drawable.ic_launcher_background),
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.errorimage)
    )
}

@Composable
fun RowIcons(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onClickLikeIcon: () -> Unit,
    onClickCommentIcon: () -> Unit,
    likes: Int,
    currentComments: Int
)
{
    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 25.dp
        WindowWidthSizeClass.Expanded -> 25.dp
        else -> 20.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        WindowWidthSizeClass.Medium -> 25.sp
        WindowWidthSizeClass.Expanded -> 25.sp
        else -> 20.sp
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        )
        {
            Text(
                text = likes.toString(),
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.tertiary
            )

            IconButton(
                onClick = onClickLikeIcon,
                modifier = Modifier.size(iconSize)
            )
            {
                Icon(
                    painter = painterResource(R.drawable.favourite),
                    contentDescription = stringResource(R.string.like_Message)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        )
        {
            Text(
                text = currentComments.toString(),
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.tertiary
            )

            IconButton(
                onClick = onClickCommentIcon,
                modifier = Modifier.size(iconSize)
            )

            {
                Icon(
                    painter = painterResource(R.drawable.comment),
                    contentDescription = stringResource(R.string.like_Message)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToOtherProfile: (UserInfo, Int) -> Unit,
    viewModel: HomeViewModel = viewModel(),
    )
{

    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()
    var selectedPost by remember { mutableStateOf<PublicacionHome?>(null) } //Para saber que publicacion mostrar al hacer click

    val gridState = rememberLazyGridState() //State para obtener informacion del lazy

    val scrollEnElFinal by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount //Obtenemos del lazy la cantidad total de elementos

            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 //Obtenemos el indice del ultimo item cargado

            //Si el ultimo elemento visible esta a 1 fila del final, da true
            //lo que activa la peticion al back
            totalItems > 0 && ultimoItem >= (totalItems - rowProfilePostSize)
        }
    }

    //LaunchedEffect(scrollEnElFinal) {
    //    if (scrollEnElFinal && stateProcess != HomeState.Cargando && !uiState.ultimaPagina) {
    //        viewModel.loadPosts()
    //    }
    //}

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = gridState,
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize(),
    )
    {
        //Poner uiState.posts
        items(listaPublicaciones) { post ->
            PostHomeCreation(
                userInfo = uiState.userInfo,
                post = post,
                windowSize = windowSize,
                onClickPostProfile = { user, id -> onNavigateToOtherProfile(user, id) },
                onClickPostPhoto = {selectedPost = post},
                onClickLikeIcon = {},
                onClickCommentIcon = {},
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            if (stateProcess is HomeState.Error) {
                Text(
                    text = stringResource((stateProcess as HomeState.Error).mensaje),
                    fontSize = 14.sp,
                    color = Color.Red
                )
            }
        }
    }

    if (selectedPost != null) {
        //ExpandedPhotoPostProfile()
    }

}
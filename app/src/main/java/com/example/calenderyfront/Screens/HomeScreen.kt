package com.example.calenderyfront.Screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.example.calenderyfront.CommentsPostWindow
import com.example.calenderyfront.ExpandedPhotoPost
import com.example.calenderyfront.Model.DataObjects.PostUIData
import com.example.calenderyfront.Model.DataObjects.PublicacionHome
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.home.HomeState
import com.example.calenderyfront.home.HomeViewModel
import com.example.calenderyfront.rowProfilePostSize

@Composable
fun PostHomeCreation(
    userInfo: UserInfo,
    photoUser: String,
    post: PublicacionHome,
    windowSize: WindowWidthSizeClass,
    @DrawableRes likeIcon: Int,
    onClickPostPhoto: () -> Unit,
    onClickPostProfile: (UserInfo, Int) -> Unit,
    onClickLikeIcon: () -> Unit,
    onClickCommentIcon: () -> Unit
)
{
    val photoUserSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 40.dp
        WindowWidthSizeClass.Medium -> 30.dp
        WindowWidthSizeClass.Expanded -> 33.dp
        else -> 20.dp
    }

    val fontSizeUser = when (windowSize) {
        WindowWidthSizeClass.Compact -> 17.sp
        WindowWidthSizeClass.Medium -> 40.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    val fontSizeMessage = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.sp
        WindowWidthSizeClass.Medium -> 30.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .widthIn(max = 600.dp),
        shape =RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp, start = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                PhotoUserContainer(
                    modifier = Modifier.size(photoUserSize),
                    photoPath = photoUser,
                    onClick =  {onClickPostProfile(userInfo, post.idUsuario)},
                    contentDescription =  R.string.post_description
                )

                Text(
                    text = post.nombreUsuario,
                    fontSize = fontSizeUser,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            if (post.fotoPublicacion != null) {
                PostHome(Modifier,post.fotoPublicacion,windowSize,{onClickPostPhoto()})
            }

            if (post.mensaje != null) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = post.mensaje,
                    fontSize = fontSizeMessage,
                    color = MaterialTheme.colorScheme.tertiary,
                    softWrap = true
                )
            }

            RowIcons(
                modifier = Modifier.fillMaxWidth(0.7F),
                windowSize = windowSize,
                likeIcon = likeIcon,
                onClickLikeIcon ={onClickLikeIcon()},
                onClickCommentIcon = {onClickCommentIcon()},
                currentLikes = post.cantidadLikes,
                currentComments = post.cantidadComentarios
            )
            Spacer(Modifier.padding(bottom = 5.dp))
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
    val aspect = when (windowSize) {
        WindowWidthSizeClass.Compact -> 1F
        else -> 1.5F
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
            .fillMaxWidth()
            .aspectRatio(aspect)
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
    @DrawableRes likeIcon: Int,
    onClickLikeIcon: () -> Unit,
    onClickCommentIcon: () -> Unit,
    currentLikes: Int,
    currentComments: Int
)
{
    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 25.dp
        WindowWidthSizeClass.Medium -> 25.dp
        WindowWidthSizeClass.Expanded -> 25.dp
        else -> 20.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.sp
        WindowWidthSizeClass.Medium -> 18.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        IconRow(
            modifier = Modifier.size(iconSize),
            onClick = onClickLikeIcon,
            icon = likeIcon,
            contentDescription = R.string.like_Message,
            quantity = currentLikes,
            fontSize = fontSize
        )

        IconRow(
            modifier = Modifier.size(iconSize),
            onClick = onClickCommentIcon,
            icon = R.drawable.comment,
            contentDescription = R.string.comment_Message,
            quantity = currentComments,
            fontSize = fontSize
        )
    }
}

@Composable
fun IconRow(
    modifier: Modifier = Modifier,
    onClick : () -> Unit,
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    quantity: Int,
    fontSize: TextUnit
)
{
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically

    )
    {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        )
        {
            Icon(
                painter = painterResource(icon),
                contentDescription = stringResource(contentDescription),
                tint = Color.Unspecified //Para que se pinte bien
            )
        }
        Text(
            text = quantity.toString(),
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.tertiary
        )
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

    var showComments by remember { mutableStateOf(false) }
    var commentsPostId by remember { mutableIntStateOf(-1) }

    val scrollEnElFinal by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount //Obtenemos del lazy la cantidad total de elementos

            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 //Obtenemos el indice del ultimo item cargado

            //Si el ultimo elemento visible esta a 1 fila del final, da true
            //lo que activa la peticion al back
            totalItems > 0 && ultimoItem >= (totalItems - rowProfilePostSize)
        }
    }

    val horizontalSpaced = when (windowSize) {
        WindowWidthSizeClass.Medium -> 16.dp
        else -> 10.dp
    }

    LaunchedEffect(scrollEnElFinal) {
        if (scrollEnElFinal && stateProcess != HomeState.Cargando && !uiState.ultimaPaginaPost && uiState.posts.isNotEmpty()) {
            viewModel.loadPosts()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = if (windowSize == WindowWidthSizeClass.Compact) GridCells.Fixed(1) else GridCells.Fixed(2),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(horizontalSpaced),
            modifier = Modifier.fillMaxSize(),
        )
        {
            items(items = uiState.posts, key = { it.idPost }) { postClick ->
                val currentPostData = uiState.posts.find { it.idPost == postClick.idPost } //Buscamos el post al que le hemos dado click
                val postToShow = currentPostData ?: postClick //Si por alguna razon no lo encontramos, usamos la copia estatica
                val favouriteIcon = if (postToShow.like) R.drawable.favourite_filled else R.drawable.favourite

                PostHomeCreation(
                    userInfo = uiState.userInfo,
                    photoUser = postToShow.fotoUsuario,
                    post = postToShow,
                    windowSize = windowSize,
                    likeIcon = favouriteIcon,
                    onClickPostProfile = { user, id -> onNavigateToOtherProfile(user, id) },
                    onClickPostPhoto = {selectedPost = postToShow},
                    onClickLikeIcon = {
                        if (!postToShow.like) viewModel.likePost(postToShow) else viewModel.unLikePost(postToShow)
                    },
                    onClickCommentIcon = {
                        commentsPostId = postToShow.idPost
                        viewModel.deleteCommentsLoaded()
                        viewModel.getCommentsPost(idPost = postToShow.idPost)
                        showComments = true
                    },
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

        if (uiState.posts.isEmpty() && stateProcess is HomeState.Cargando) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        selectedPost?.let { postClick ->
            val currentPostData = uiState.posts.find { it.idPost == postClick.idPost } //Buscamos el post al que le hemos dado click
            val postToShow = currentPostData ?: postClick //Si por alguna razon no lo encontramos, usamos la copia estatica

            val favouriteIcon = if (postToShow.like) R.drawable.favourite_filled else R.drawable.favourite
            ExpandedPhotoPost(
                post = PostUIData(
                    postId = postToShow.idPost,
                    fotoPublicacion = postToShow.fotoPublicacion,
                    mensaje = postToShow.mensaje,
                    cantidadLikes = postToShow.cantidadLikes,
                    cantidadComentarios = postToShow.cantidadComentarios
                ),
                likeIcon = favouriteIcon,
                onDismiss = {
                    selectedPost = null
                    viewModel.deleteCommentsLoaded()
                },
                onClickLikes = {
                    if (!postToShow.like) viewModel.likePost(postToShow) else viewModel.unLikePost(postToShow)
                },
                onClickComments = {
                    commentsPostId = postToShow.idPost
                    viewModel.getCommentsPost(idPost = postToShow.idPost)
                    showComments = true
                },
                windowSize = windowSize
            )
        }

        if (showComments) {
            CommentsPostWindow(
                currentComment = uiState.comment,
                commentsList = uiState.listComments,
                isLastPage = uiState.ultimaPaginaComment,
                onClose = {
                    showComments = false
                },
                onLoadMoreComments = {  viewModel.getCommentsPost(commentsPostId)  },
                onClickPhoto = { idUserComment ->
                    onNavigateToOtherProfile(uiState.userInfo, idUserComment)
                },
                onCommentChange = { viewModel.onCommentChange(it) },
                onSendComment = {
                    viewModel.sendCommentToPost(commentsPostId)
                },
                windowSize = windowSize,
            )
        }
    }


}
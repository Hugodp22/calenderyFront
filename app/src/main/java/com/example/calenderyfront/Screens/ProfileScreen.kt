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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.calenderyfront.ExpandedPhotoPostProfile
import com.example.calenderyfront.ExpandedPhotoProfile
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.TimeData
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.profile.ProfileState
import com.example.calenderyfront.profile.ProfileViewModel
import com.example.calenderyfront.rowProfilePostSize
import com.example.calenderyfront.ui.theme.BebasNeue
import com.example.calenderyfront.ui.theme.FjalaOne
import java.time.Instant
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale.getDefault
import java.util.SortedMap

val publicacionesDePrueba = listOf(
    PublicacionProfile(
        id = 1,
        fotoPublicacion = "https://picsum.photos/id/10/500/500",
        mensaje = "Publicación de la tarde",
        cantidadLikes = 120,
        fechaCalendario = LocalDate.of(2026, 4, 20),
        fechaPublicacion = Instant.parse("2026-04-20T18:30:00Z")
    ),
    PublicacionProfile(
        id = 2,
        fotoPublicacion = "https://picsum.photos/id/20/500/500",
        mensaje = "Publicación de la mañana",
        cantidadLikes = 85,
        fechaCalendario = LocalDate.of(2026, 4, 20),
        fechaPublicacion = Instant.parse("2026-04-20T09:15:00Z")
    ),

    PublicacionProfile(
        id = 3,
        fotoPublicacion = "https://picsum.photos/id/30/500/500",
        mensaje = "Día de relax",
        cantidadLikes = 250,
        fechaCalendario = LocalDate.of(2026, 4, 20),
        fechaPublicacion = Instant.now().minus(1, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 4,
        fotoPublicacion = "https://picsum.photos/id/40/500/500",
        mensaje = "Café mañanero",
        cantidadLikes = 45,
        fechaCalendario = LocalDate.of(2026, 4, 20),
        fechaPublicacion = Instant.now().minus(2, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 5,
        fotoPublicacion = "https://picsum.photos/id/50/500/500",
        mensaje = "Nuevas metas",
        cantidadLikes = 300,
        fechaCalendario = LocalDate.of(2026, 4, 17),
        fechaPublicacion = Instant.now().minus(3, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 6,
        fotoPublicacion = "https://picsum.photos/id/60/500/500",
        mensaje = "Vistas increíbles",
        cantidadLikes = 156,
        fechaCalendario = LocalDate.of(2026, 4, 16),
        fechaPublicacion = Instant.now().minus(4, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 7,
        fotoPublicacion = "https://picsum.photos/id/70/500/500",
        mensaje = "Working hard",
        cantidadLikes = 92,
        fechaCalendario = LocalDate.of(2026, 4, 15),
        fechaPublicacion = Instant.now().minus(5, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 8,
        fotoPublicacion = "https://picsum.photos/id/82/500/500",
        mensaje = "TBT de vacaciones",
        cantidadLikes = 410,
        fechaCalendario = LocalDate.of(2026, 4, 14),
        fechaPublicacion = Instant.now().minus(6, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 9,
        fotoPublicacion = "https://picsum.photos/id/90/500/500",
        mensaje = "Cena con amigos",
        cantidadLikes = 112,
        fechaCalendario = LocalDate.of(2026, 4, 13),
        fechaPublicacion = Instant.now().minus(7, ChronoUnit.DAYS)
    ),
    PublicacionProfile(
        id = 10,
        fotoPublicacion = "https://picsum.photos/id/102/500/500",
        mensaje = "Último post del pack",
        cantidadLikes = 67,
        fechaCalendario = LocalDate.of(2026, 4, 12),
        fechaPublicacion = Instant.now().minus(8, ChronoUnit.DAYS)
    ),
    //Publicacion en mes diferente para ver si funciona, espero que si
    PublicacionProfile(id = 11, fotoPublicacion = "https://picsum.photos/id/102/500/500", mensaje = "Último post del pack", cantidadLikes = 67, fechaCalendario = LocalDate.of(2026, 3, 12), fechaPublicacion = Instant.now().minus(8, ChronoUnit.DAYS))
)

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    userName: String,
    photoUser: String,
    onClickPhoto: () -> Unit,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    description: String?,
    numberOfFollowers: Int = 0,
    numberOfFollowed: Int = 0,
    otherUser: Boolean
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
        WindowWidthSizeClass.Medium -> 20.sp
        WindowWidthSizeClass.Expanded -> 15.sp
        else -> 15.sp
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
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
                PhotoUserContainer(Modifier.size(photoSize), photoUser, { onClickPhoto() }, R.string.User_profile_foto)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    ProfileStat(windowSize, numberOfFollowers, R.string.followers_text)
                    ProfileStat(windowSize, numberOfFollowed, R.string.followed_text)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.onPrimary),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Column(
                    modifier = Modifier.weight(1f)
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
                            modifier = Modifier.padding(top = 7.dp),
                            text = description,
                            fontSize = fontSizeName,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(Modifier.padding(bottom = 16.dp))

            if (otherUser) {
                ButtonsBox(
                    modifier = Modifier.padding(start = 8.dp),
                    windowSize = windowSize,
                    buttonLeft = R.string.follow_user,
                    buttonRight = R.string.message_user,
                    onClickLeft = onClickLeft,
                    onClickRight = onClickRight
                )
                Spacer(Modifier.padding(bottom = 16.dp))
            }
        }

        if (!otherUser) {
            IconsBox(
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 34.dp, end = 18.dp),
                windowSize = windowSize,
                leftIcon = R.drawable.upload,
                descriptionLeft = R.string.upload_message,
                rightIcon = R.drawable.settings,
                descriptionRight = R.string.settings_profile,
                onClickLeft =  onClickLeft,
                onClickRight = onClickRight,
            )
        }
    }
}

@Composable
fun ButtonsBox(
    modifier : Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    @StringRes buttonLeft: Int,
    @StringRes buttonRight: Int,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
)
{
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 150.dp
        WindowWidthSizeClass.Medium -> 210.dp
        WindowWidthSizeClass.Expanded -> 200.dp
        else -> 130.dp
    }

    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 35.dp
        WindowWidthSizeClass.Medium -> 50.dp
        WindowWidthSizeClass.Expanded -> 45.dp
        else -> 46.dp
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 14.sp
        WindowWidthSizeClass.Medium -> 22.sp
        WindowWidthSizeClass.Expanded -> 20.sp
        else -> 10.sp
    }

    val spacedBy = when (windowSize) {
        WindowWidthSizeClass.Compact -> 13.dp
        WindowWidthSizeClass.Medium -> 25.dp
        WindowWidthSizeClass.Expanded -> 13.dp
        else -> 46.dp
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    )
    {
        Button(
            modifier = Modifier.size(width = width, height = height),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContentColor = Color.Gray,
            ),
            onClick = onClickLeft
        )
        {
            Text(
                text = stringResource(buttonLeft),
                fontSize = fontSize,
                maxLines = 1,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Button(
            modifier = Modifier.size(width = width, height = height),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContentColor = Color.Gray,
            ),
            onClick = onClickRight
        )
        {
            Text(
                text = stringResource(buttonRight),
                fontSize = fontSize,
                maxLines = 1,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun IconsBox(
    modifier : Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    @DrawableRes leftIcon: Int,
    @StringRes descriptionLeft: Int,
    @DrawableRes rightIcon: Int,
    @StringRes descriptionRight: Int,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
)
{
    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 30.dp
        WindowWidthSizeClass.Medium -> 50.dp
        WindowWidthSizeClass.Expanded -> 50.dp
        else -> 30.dp
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = onClickLeft
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(leftIcon),
                contentDescription = stringResource(descriptionLeft),
            )
        }

        IconButton(
            modifier = Modifier.size(iconSize),
            onClick = onClickRight
        )
        {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(rightIcon),
                contentDescription = stringResource(descriptionRight)
            )
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
fun WeekTitle(
    timeData: TimeData,
    windowSize: WindowWidthSizeClass
)
{
    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 24.sp
        WindowWidthSizeClass.Medium -> 34.sp
        WindowWidthSizeClass.Expanded -> 36.sp
        else -> 24.sp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.Start,
    )
    {
        Text(
            text = stringResource(R.string.week_text) + timeData.semana,
            fontSize = fontSize,
            fontFamily = BebasNeue,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun PostProfile(
    modifier: Modifier = Modifier,
    post: PublicacionProfile,
    onClick : () -> Unit
)
{
    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clickable { onClick() }
    )
    {
        AsyncImage(
            model = post.fotoPublicacion,
            contentDescription = stringResource(R.string.post_description) + post.mensaje,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MonthTitle(
    localDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    canGoNext : Boolean,
    windowSize: WindowWidthSizeClass
)
{
    val width = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.7F
        WindowWidthSizeClass.Medium -> 0.5F
        WindowWidthSizeClass.Expanded -> 0.55F
        else -> 0.7F
    }

    val fontSizeTitle = when (windowSize) {
        WindowWidthSizeClass.Compact -> 35.sp
        WindowWidthSizeClass.Medium -> 50.sp
        WindowWidthSizeClass.Expanded -> 50.sp
        else -> 25.sp
    }

    val fontSizeYear = when (windowSize) {
        WindowWidthSizeClass.Compact -> 22.sp
        WindowWidthSizeClass.Medium -> 33.sp
        WindowWidthSizeClass.Expanded -> 33.sp
        else -> 22.sp
    }

    val fontSizeArrows = when (windowSize) {
        WindowWidthSizeClass.Compact -> 35.sp
        WindowWidthSizeClass.Medium -> 50.sp
        WindowWidthSizeClass.Expanded -> 50.sp
        else -> 35.sp
    }

    val monthTitle =
        localDate.month.getDisplayName(TextStyle.FULL, getDefault()).uppercase(getDefault())

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    )
    {
        Card(
            modifier = Modifier.fillMaxWidth(width),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text(
                    text = stringResource(R.string.left_arrow),
                    modifier = Modifier.clickable { onPreviousMonth() },
                    fontSize = fontSizeArrows,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        text = monthTitle,
                        fontFamily = FjalaOne,
                        fontSize = fontSizeTitle,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = localDate.year.toString(),
                        fontSize = fontSizeYear,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Text(
                    text = stringResource(R.string.right_arrow),
                    modifier = Modifier.then(if (canGoNext) Modifier.clickable { onNextMonth() } else Modifier),
                    fontSize = fontSizeArrows,
                    color = if (canGoNext) MaterialTheme.colorScheme.tertiary else Color.Gray
                )
            }
        }
    }
}

/**
 * Funcion para ordenar las publicaciones, mediante su mes, y dentro del mes, mediante numero de semana
 */
fun getGroupedPosts(publicaciones: List<PublicacionProfile>): SortedMap<LocalDate, SortedMap<TimeData, List<PublicacionProfile>>> {
    //Obtenemos cuando acaba una semana a nivel regional
    val weekFields = WeekFields.of(getDefault())

    return publicaciones
        .sortedBy { it.fechaCalendario } // Ordenamos por fecha de calendario
        .groupBy { it.fechaCalendario.withDayOfMonth(1) } // Agrupamos por el inicio del mes
        //Ordenamos cada mes
        .mapValues { monthEntry ->
            monthEntry.value //Obtenemos el mes
                .groupBy { post ->
                    // Calculamos el numero de semana del mes
                    val numSemana = post.fechaCalendario.get(weekFields.weekOfMonth())

                    TimeData(
                        anio = post.fechaCalendario.year,
                        semana = numSemana,
                        fechaReferencia = monthEntry.key //Obtenemos el dia 1 de ese mes
                    )
                }
                .mapValues { weekEntry ->
                    weekEntry.value.sortedByDescending { it.fechaPublicacion } //Ordenamos por fecha de publicacion dentro de una misma semana
                }
                .toSortedMap(compareBy { it.semana }) //Ordenamos las semanas
        }
        .toSortedMap(compareByDescending { it })
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToSettings: (UserInfo) -> Unit,
    onNavigateToUpload: (UserInfo) -> Unit,
    onNavigateToChat: (UserInfo,Int) -> Unit,
    viewModel: ProfileViewModel = viewModel(),
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val gridState = rememberLazyGridState() //State para obtener informacion del lazy

    var expandedPhotoProfile by remember { mutableStateOf(false) } //Para saber cuando la foto esta ampliada
    var selectedPost by remember { mutableStateOf<PublicacionProfile?>(null) } //Para saber que publicacion mostrar al hacer click

    var selectedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) } //Mes que puede variar
    val realCurrentMonth = remember { LocalDate.now().withDayOfMonth(1) } //Mes actual de la vida real
    val canGoNext = selectedMonth.isBefore(realCurrentMonth) //Comprobamos siempre, si el mes siguiente va antes del actual

    val otherUser = uiState.otherUserId != null //Para saber si hemos entrado en el perfil nuestro o de otro usuario


    //Para detectar cuando el scroll esta en el final
    val scrollEnElFinal by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount //Obtenemos del lazy la cantidad total de elementos

            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 //Obtenemos el indice del ultimo item cargado

            //Si el ultimo elemento visible esta a 1 fila del final, da true
            //lo que activa la peticion al back
            totalItems > 0 && ultimoItem >= (totalItems - rowProfilePostSize)
        }
    }

    LaunchedEffect(stateProcess) {
        if (stateProcess is ProfileState.Exito) {
            onNavigateToSettings((stateProcess as ProfileState.Exito).userInfo) //Cambiar obvio a otra cosa
        }
    }

    LaunchedEffect(selectedMonth) {
        gridState.scrollToItem(0) //Cuando cambiemos de mes, vamos al indice 0 para volver arriba y no quedarnos abajo
        //viewModel.loadPublicationsByDate(selectedMonth.year, selectedMonth.monthValue)
    }

    //Si estamos en el final, no esta cargando, y aun no es la ultima pagina de ese mes, cargamos publicaciones
    //LaunchedEffect(scrollEnElFinal) {
    //    if (scrollEnElFinal && stateProcess != ProfileState.Cargando && !uiState.ultimaPagina) {
    //        viewModel.loadPublicationsByDate(selectedMonth.year, selectedMonth.monthValue)
    //    }
    //}

    val groupedPost = remember(publicacionesDePrueba) {
        getGroupedPosts(publicacionesDePrueba)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(rowProfilePostSize), //Columnas de 3 en 3 publicaciones. Para el tamaño y eso
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxSize(),
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
                onClickLeft = {
                    if (otherUser) {
                        viewModel.followUser()
                    }
                    else {
                        onNavigateToUpload(uiState.usuario)
                    }
                                  },
                onClickRight = {
                    if (otherUser) {
                        onNavigateToChat(uiState.usuario,uiState.mainId)
                    }
                    else {
                        onNavigateToSettings(uiState.usuario)
                    }
                                   },
                description = uiState.descripcion,
                numberOfFollowers = uiState.cantidadSeguidores,
                numberOfFollowed = uiState.cantidadSeguidos,
                otherUser = otherUser
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MonthTitle(
                localDate = selectedMonth,
                windowSize = windowSize,
                onPreviousMonth = { selectedMonth = selectedMonth.minusMonths(1) }, //Va restando todo, incluido año al llegar
                onNextMonth = {
                    if (canGoNext) {
                        selectedMonth = selectedMonth.plusMonths(1)
                    }
                },
                canGoNext = canGoNext
            )
        }

        val currentMonthData = groupedPost[selectedMonth]

            if (currentMonthData != null) {
                currentMonthData.forEach { (weekOfMonth, postsOfWeek) ->

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        WeekTitle(timeData = weekOfMonth, windowSize = windowSize)
                    }

                    items(postsOfWeek, key = { it.id }) { post ->
                        PostProfile(post = post, onClick = { selectedPost = post })
                    }

                }
            }
            else {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(
                            text = stringResource(R.string.no_post_month_message),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
    }

    //Si le has dado click a la foto de perfil, se amplia
    if (expandedPhotoProfile) {
        ExpandedPhotoProfile(
            photoPath = uiState.fotoUsuario,
            onDismiss = { expandedPhotoProfile = false }
        )
    }

    //Si le has dado click a una publicacion
    selectedPost?.let {
        ExpandedPhotoPostProfile(
            post = it,
            onDismiss = { selectedPost = null },
            onClickLikes = {},
            onClickComments = {},
            windowSize = windowSize
        )
    }

    if (stateProcess is ProfileState.Cargando) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
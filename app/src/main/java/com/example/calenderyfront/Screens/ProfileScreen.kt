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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.example.calenderyfront.CommentsPostWindow
import com.example.calenderyfront.ExpandedPhotoPost
import com.example.calenderyfront.ExpandedPhotoProfile
import com.example.calenderyfront.Model.DataObjects.PostUIData
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.TimeData
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.datePastLimit
import com.example.calenderyfront.profile.ProfileState
import com.example.calenderyfront.profile.ProfileViewModel
import com.example.calenderyfront.rowProfilePostSize
import com.example.calenderyfront.ui.theme.BebasNeue
import com.example.calenderyfront.ui.theme.FjalaOne
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale.getDefault

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    stateProcess: ProfileState,
    userName: String,
    photoUser: String,
    onClickPhoto: () -> Unit,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    description: String?,
    numberOfFollowers: Int = 0,
    numberOfFollowed: Int = 0,
    otherUser: Boolean,
    follow : Boolean
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
        WindowWidthSizeClass.Expanded -> 22.sp
        else -> 22.sp
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = width)
                .padding(top = 32.dp)
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
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.padding(start = 3.dp),
                    stateProcess = stateProcess,
                    windowSize = windowSize,
                    buttonLeft = R.string.follow_user,
                    buttonRight = R.string.message_user,
                    onClickLeft = onClickLeft,
                    onClickRight = onClickRight,
                    follow = follow
                )
                Spacer(Modifier.padding(bottom = 16.dp))
            }
        }

        if (!otherUser) {
            IconsBox(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 14.dp, end = 18.dp),
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
    stateProcess: ProfileState,
    @StringRes buttonLeft: Int,
    @StringRes buttonRight: Int,
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    follow: Boolean
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
        WindowWidthSizeClass.Medium -> 13.dp
        WindowWidthSizeClass.Expanded -> 13.dp
        else -> 13.dp
    }

    val colorsLeftButton = when (follow) {
        true -> buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiary,
        )
        else -> buttonColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.tertiary,
        )
    }

    val loadingFollowColors = when (follow) {
        false -> buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.Gray
        )
        else -> buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = Color.Gray
        )
    }

    val loadingChatColors = when (stateProcess) {
        ProfileState.ChatCargando -> buttonColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        else -> buttonColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.onSecondary
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    )
    {
        Button(
            modifier = Modifier.size(width = width, height = height),
            colors = if (stateProcess is ProfileState.Siguiendo) loadingFollowColors else colorsLeftButton,
            onClick = onClickLeft
        )
        {
            Text(
                text = if (!follow) stringResource(buttonLeft) else stringResource(R.string.unfollow_text),
                fontSize = fontSize,
                maxLines = 1,
            )
        }
        Button(
            modifier = Modifier.size(width = width, height = height),
            colors = loadingChatColors,
            onClick = onClickRight
        )
        {
            Text(
                text = stringResource(buttonRight),
                fontSize = fontSize,
                maxLines = 1,
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
        WindowWidthSizeClass.Medium -> 50.sp
        WindowWidthSizeClass.Expanded -> 46.sp
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
    windowSize: WindowWidthSizeClass,
    post: PublicacionProfile,
    onClick : () -> Unit
)
{
    val photoSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 1F
        WindowWidthSizeClass.Medium -> 0.9F
        WindowWidthSizeClass.Expanded -> 0.9F
        else -> 1F
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(photoSize)
            .clickable { onClick() }
    )
    {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(post.fotoPublicacion).crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .diskCacheKey(post.id.toString())
                .precision(Precision.INEXACT)
                .build(),
            contentDescription = stringResource(R.string.post_description) + post.mensaje,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MonthTitle(
    localDate: LocalDate,
    onClickTitle : () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    canGoNext : Boolean,
    canGoPrevious : Boolean,
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

    val monthTitle = localDate.month.getDisplayName(TextStyle.FULL, getDefault()).uppercase(getDefault())

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    )
    {
        Card(
            modifier = Modifier.fillMaxWidth(width),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
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
                    modifier = Modifier.then(if (canGoPrevious) Modifier.clickable { onPreviousMonth() } else Modifier),
                    fontSize = fontSizeArrows,
                    color = if (canGoPrevious) MaterialTheme.colorScheme.tertiary else Color.Gray,
                )

                Column(
                    modifier = Modifier.clickable {onClickTitle()},
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
                    modifier = Modifier.then(if (canGoNext) Modifier.clickable { onNextMonth() } else Modifier),
                    color = if (canGoNext) MaterialTheme.colorScheme.tertiary else Color.Gray,
                    text = stringResource(R.string.right_arrow),
                    fontSize = fontSizeArrows,
                )
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    val currentMonth = initialDate.monthValue
    val realCurrentYear = LocalDate.now().year

    val fontSizeTitle = when (windowSize) {
        WindowWidthSizeClass.Compact -> 30.sp
        else -> 35.sp
    }

    val fontSizeButton = when (windowSize) {
        WindowWidthSizeClass.Compact -> 17.sp
        else -> 22.sp
    }

    val spacer = when (windowSize) {
        WindowWidthSizeClass.Compact -> 10.dp
        else -> 15.dp
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = stringResource(R.string.select_year_text),
                    fontSize = fontSizeTitle,
                    fontFamily = BebasNeue,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(Modifier.padding(bottom = spacer))

                InputYearSelection(
                    currentYear = selectedYear,
                    modifier = Modifier,
                    futureYearLimit = realCurrentYear,
                    onValueChange = { selectedYear = it }
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            )
            {
                TextButton(
                    onClick = {
                        if (selectedYear < datePastLimit) {
                            selectedYear = datePastLimit
                        }
                        else if (selectedYear > realCurrentYear) {
                            selectedYear = realCurrentYear
                        }
                        onConfirm(LocalDate.of(selectedYear, currentMonth, 1))
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContentColor = Color.Red,
                        disabledContainerColor = Color.Gray
                    )
                )
                {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = stringResource(R.string.accept),
                        fontSize = fontSizeButton,
                    )
                }
            }
        }
    )
}

@Composable
fun InputYearSelection(
    modifier: Modifier = Modifier,
    currentYear: Int,
    futureYearLimit: Int,
    onValueChange: (Int) -> Unit,
)
{
    var yearValue by remember(currentYear) { mutableStateOf(currentYear.toString()) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        TextField(
            modifier = Modifier,
            value = yearValue,
            onValueChange =  { currentYear ->
                if (currentYear.isEmpty()) {
                    yearValue = ""
                }
                else {
                    val yearInt = currentYear.toIntOrNull()
                    yearInt?.let {
                        if (yearInt > futureYearLimit) {
                            yearValue = futureYearLimit.toString()
                            onValueChange(futureYearLimit)
                        }
                        else {
                            yearValue = currentYear
                            onValueChange(yearInt)
                        }
                    }
                }
            },
            placeholder = { stringResource(R.string.introduce_year) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp) //Si lo importo da error con otro
            )
    }
}

/**
 * Función para ordenar las publicaciones, mediante su mes, y dentro del mes, mediante número de semana
 */
fun getGroupedPosts(posts: List<PublicacionProfile>): Map<LocalDate, Map<TimeData, List<PublicacionProfile>>> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val weekFields = WeekFields.of(getDefault())

    return posts
        .groupBy { post ->
            val fechaCal = LocalDate.parse(post.fechaCalendario, formatter)
            fechaCal.withDayOfMonth(1)
        }
        .toSortedMap(compareByDescending { it })
        .mapValues { (monthKey, postsInMonth) ->
            postsInMonth
                .groupBy { post ->
                    val fechaCal = LocalDate.parse(post.fechaCalendario, formatter)
                    val numSemana = fechaCal.get(weekFields.weekOfMonth()).coerceAtLeast(1)

                    TimeData(
                        anio = fechaCal.year,
                        semana = numSemana,
                        fechaReferencia = monthKey
                    )
                }
                .toSortedMap(compareBy { it.semana })
                .mapValues { (_, postsInWeek) ->
                    postsInWeek.sortedByDescending { post ->
                        LocalDate.parse(post.fechaCalendario, formatter)
                    }
                }
        }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToSettings: (UserInfo) -> Unit,
    onNavigateToUpload: (UserInfo) -> Unit,
    onNavigateToChat: (UserInfo,Int,Int,String,String) -> Unit,
    onNavigateToOtherProfile: (UserInfo, Int) -> Unit,
    viewModel: ProfileViewModel = viewModel(),
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val gridState = rememberLazyGridState() //State para obtener informacion del lazy

    var expandedPhotoProfile by remember { mutableStateOf(false) } //Para saber cuando la foto esta ampliada
    var selectedPost by remember { mutableStateOf<PublicacionProfile?>(null) } //Copia estatica de la publicacion a la que le des click

    var showDatePicker by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var commentsPostId by remember { mutableIntStateOf(-1) }

    var selectedDate by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) } //Mes que puede variar
    val realCurrentMonth = remember { LocalDate.now().withDayOfMonth(1) } //Mes actual de la vida real

    val canGoNext = selectedDate.isBefore(realCurrentMonth) //Comprobamos siempre, si el mes siguiente va antes del actual
    val canGoPrevious = selectedDate.isAfter(LocalDate.ofYearDay(1826,1))

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
            onNavigateToSettings((stateProcess as ProfileState.Exito).userInfo)
        }
        else if (stateProcess is ProfileState.ChatExito) {
            uiState.chatId?.let { safeChatId ->
                onNavigateToChat(
                    uiState.usuario,
                    uiState.mainId,
                    safeChatId,
                    uiState.nombreUsuario,
                    uiState.fotoUsuario
                )
            }
            viewModel.restartState()
        }
    }

    LaunchedEffect(selectedDate) {
        gridState.scrollToItem(0) //Cuando cambiemos de mes, vamos al indice 0 para volver arriba y no quedarnos abajo
        viewModel.loadPublicationsByDate(selectedDate.year, selectedDate.monthValue)
    }

    //Si estamos en el final, no esta cargando, y aun no es la ultima pagina de ese mes, cargamos publicaciones
    LaunchedEffect(scrollEnElFinal) {
        if (scrollEnElFinal && stateProcess != ProfileState.Cargando && !uiState.ultimaPaginaPosts && uiState.publicaciones.isNotEmpty()) {
            viewModel.loadPublicationsByDate(selectedDate.year, selectedDate.monthValue)
        }
    }

    val groupedPost = remember(uiState.publicaciones) {
        getGroupedPosts(uiState.publicaciones)
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
                stateProcess = stateProcess,
                userName = uiState.nombreUsuario,
                photoUser = uiState.fotoUsuario,
                onClickPhoto = { expandedPhotoProfile = true },
                onClickLeft = {
                    if (otherUser) {
                        if (uiState.seguidor) viewModel.unFollowUser() else viewModel.followUser()
                    }
                    else {
                        onNavigateToUpload(uiState.usuario)
                    }
                              },
                onClickRight = {
                    if (otherUser) {
                        if (uiState.existeChat) viewModel.getChatId() else viewModel.createChat(otherUserId = uiState.mainId)
                    }
                    else {
                        onNavigateToSettings(uiState.usuario)
                    }
                                   },
                description = uiState.descripcion,
                numberOfFollowers = uiState.cantidadSeguidores,
                numberOfFollowed = uiState.cantidadSeguidos,
                otherUser = otherUser,
                follow = uiState.seguidor
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MonthTitle(
                localDate = selectedDate,
                windowSize = windowSize,
                onClickTitle = { showDatePicker = true },
                onPreviousMonth = {
                    selectedDate = selectedDate.minusMonths(1)
                    if (canGoPrevious) {
                        viewModel.loadPublicationsByDate(selectedDate.year,selectedDate.monthValue)
                    }
                                  },
                onNextMonth = {
                    if (canGoNext) {
                        selectedDate = selectedDate.plusMonths(1)
                        viewModel.loadPublicationsByDate(selectedDate.year,selectedDate.monthValue)
                    }
                },
                canGoNext = canGoNext,
                canGoPrevious = canGoPrevious
            )
        }

        val currentMonthData = groupedPost[selectedDate]

            if (currentMonthData != null) {
                currentMonthData.forEach { (weekOfMonth, postsOfWeek) ->

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        WeekTitle(timeData = weekOfMonth, windowSize = windowSize)
                    }

                    items(postsOfWeek, key = { it.id }) { post ->
                        PostProfile(
                            post = post,
                            windowSize = windowSize,
                            onClick = { selectedPost = post })
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
                        if (stateProcess is ProfileState.Cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(250.dp),
                                color = MaterialTheme.colorScheme.onTertiary,
                                strokeWidth = 12.dp
                            )
                        }
                        else if (stateProcess is ProfileState.NoPublicaciones) {
                            Text(
                                text = stringResource(R.string.no_post_month_message),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
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
    selectedPost?.let { postClick ->
        val currentPostData = uiState.publicaciones.find { it.id == postClick.id } //Buscamos el post al que le hemos dado click
        val postToShow = currentPostData ?: postClick //Si por alguna razon no lo encontramos, usamos la copia estatica

        val favouriteIcon = if (postToShow.like) R.drawable.favourite_filled else R.drawable.favourite
        ExpandedPhotoPost(
            post = PostUIData(
                postId = postToShow.id,
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
                commentsPostId = postToShow.id
                viewModel.getCommentsPost(idPost = postToShow.id)
                showComments = true
            },
            windowSize = windowSize
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { dateSelected ->
                selectedDate = dateSelected
                viewModel.loadPublicationsByDate(selectedDate.year, selectedDate.monthValue)
                showDatePicker = false
            },
            windowSize = windowSize
        )
    }

    if (showComments) {
        CommentsPostWindow(
            currentComment = uiState.comment,
            commentsList = uiState.comentarios,
            isLastPage = uiState.ultimaPaginaComments,
            onClose = {
                showComments = false
            },
            onLoadMoreComments = {  viewModel.getCommentsPost(commentsPostId)  },
            onClickPhoto = { idUserComment ->
                onNavigateToOtherProfile(uiState.usuario, idUserComment)
            },
            onCommentChange = { viewModel.onCommentChange(it) },
            onSendComment = {
                viewModel.sendCommentToPost(commentsPostId)
            },
            windowSize = windowSize,
        )
    }
}
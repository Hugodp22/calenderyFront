package com.example.calenderyfront.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.DataObjects.SelectionUserChatData
import com.example.calenderyfront.Model.DataObjects.SelectionUserProfileData
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.selection.SelectionState
import com.example.calenderyfront.selection.SelectionViewModel

@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 17.sp
        else -> 17.sp
    }

    val width  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        else -> 0.8F
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = Modifier.fillMaxWidth(width),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_input_placeholder),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSize
            )
                      },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide() //Para que se oculte el teclado al dar a buscar
            }
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun UserSelectionProfileInfo(
    userSelectionUserData: SelectionUserProfileData,
    onClickProfile: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val width  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        else -> 0.8F
    }

    val photoUserSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 40.dp
        else -> 45.dp
    }

    val fontSizeName  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.sp
        else -> 25.sp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(width)
            .clickable { onClickProfile() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        PhotoUserContainer(
            modifier = Modifier.size(photoUserSize).padding(start = 5.dp),
            photoPath = userSelectionUserData.fotoPerfil,
            onClick = onClickProfile,
            contentDescription = R.string.profile_search
        )

        Spacer(Modifier.padding(end = 7.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        )
        {
            Text(
                text = userSelectionUserData.nombre,
                fontSize = fontSizeName,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun UserSelectionChatInfo(
    userSelectionUserData: SelectionUserChatData,
    onClickContact: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val width  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.8F
        else -> 0.8F
    }

    val photoUserSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 40.dp
        else -> 45.dp
    }

    val iconSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.dp
        else -> 25.dp
    }

    val fontSizeName  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.sp
        else -> 20.sp
    }

    val fontSizeLastMessage  = when (windowSize) {
        WindowWidthSizeClass.Compact -> 15.sp
        else -> 15.sp
    }

    val color = when (userSelectionUserData.mensajeNuevo) {
        true -> MaterialTheme.colorScheme.onPrimary
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(width)
            .clickable { onClickContact() }
            .background(color),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        PhotoUserContainer(
            modifier = Modifier.size(photoUserSize).padding(start = 5.dp),
            photoPath = userSelectionUserData.fotoPerfil,
            onClick = onClickContact,
            contentDescription = R.string.profile_search
        )

        Spacer(Modifier.padding(end = 7.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        )
        {
            Text(
                text = userSelectionUserData.nombre,
                fontSize = fontSizeName,
                color = MaterialTheme.colorScheme.onSurface
            )

             Row(
                 modifier = Modifier.fillMaxWidth(width),
                 horizontalArrangement = Arrangement.spacedBy(7.dp),
                )
             {
                 ChatExtraData(
                     modifier = Modifier.weight(1f, fill = false),
                     lastMessage = userSelectionUserData.ultimoMensaje,
                     newMessage = userSelectionUserData.mensajeNuevo ?: false,
                     fontSize = fontSizeLastMessage,
                     iconSize = iconSize
                 )
             }
        }
    }
}

@Composable
fun ChatExtraData(
    modifier: Modifier = Modifier,
    lastMessage: String?,
    newMessage: Boolean,
    fontSize: TextUnit,
    iconSize : Dp
)
{
    if (lastMessage != null) {
        Text(
            modifier = modifier,
            text = lastMessage,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    if (newMessage) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(R.drawable.new_label),
            contentDescription = stringResource(R.string.new_message_selection),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}

@Composable
fun SelectionScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    onNavigateToOtherProfile: (UserInfo,Int) -> Unit,
    onNavigateToChat: (UserInfo,Int,Int,String,String) -> Unit,
    viewModel: SelectionViewModel = viewModel()
)
{
    val uiState by viewModel.uiState.collectAsState()
    val stateProcess by viewModel.state.collectAsState()

    val gridState = rememberLazyGridState() //State para obtener informacion del lazy

    val chatOption = uiState.chatOption

    val scrollEnElFinal by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val ultimoItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && ultimoItem >= (totalItems - 2)
        }
    }

    LaunchedEffect(scrollEnElFinal) {
        if (scrollEnElFinal && stateProcess != SelectionState.Cargando && !uiState.lastPage) {
            viewModel.loadNextPage()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = gridState,
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(25.dp),
        modifier = modifier.fillMaxSize(),
    )
    {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SearchInput(
                value = uiState.searchName,
                onValueChange = { viewModel.onSearchChange(it) },
                onSearch = { viewModel.loadNextPage() },
                windowSize = windowSize
            )
        }

        if (chatOption) {
            items(items = uiState.selectionContactsList) { user ->
                UserSelectionChatInfo(
                    userSelectionUserData = user,
                    windowSize = windowSize,
                    onClickContact = {
                        viewModel.markContactAsRead(idChat = user.idChat)
                        onNavigateToChat(uiState.userInfo, user.idUsuario,user.idChat, user.nombre, user.fotoPerfil)
                                     },
                )
            }
        }
        else {
            items(items = uiState.selectionProfilesList) { user ->
                UserSelectionProfileInfo(
                    userSelectionUserData = user,
                    windowSize = windowSize,
                    onClickProfile = {
                        onNavigateToOtherProfile(uiState.userInfo, user.idUsuario)
                    }
                )
            }
        }

    }

}
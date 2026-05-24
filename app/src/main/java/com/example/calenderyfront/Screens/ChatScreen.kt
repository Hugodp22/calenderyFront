package com.example.calenderyfront.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.DataObjects.MessageResponseDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.PhotoUserContainer
import com.example.calenderyfront.R
import com.example.calenderyfront.chat.ChatState
import com.example.calenderyfront.chat.ChatViewModel
import com.example.calenderyfront.ui.theme.LocalCustomColors

@Composable
fun ChatTopBar(
    userName: String,
    userPhoto: String,
    onNavigateToOtherProfile: () -> Unit,
    windowSize: WindowWidthSizeClass
)
{
    val height = when (windowSize) {
        WindowWidthSizeClass.Compact -> 0.09F
        else -> 0.09F
    }

    val fontSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 20.sp
        else -> 20.sp
    }

    val photoSize = when (windowSize) {
        WindowWidthSizeClass.Compact -> 45.dp
        else -> 45.dp
    }

    val colors = LocalCustomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(height)
            .background(color = colors.chatTopbarBackground),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        PhotoUserContainer(
            modifier = Modifier.size(photoSize).padding(end = 5.dp),
            photoPath = userPhoto,
            onClick = onNavigateToOtherProfile,
            contentDescription = R.string.go_other_profile
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = userName,
            fontSize = fontSize,
            color = colors.chatTopbarContent
        )
    }

}

@Composable
fun MessageItem(
    message: MessageResponseDto,
    isMine: Boolean,
    windowSize: WindowWidthSizeClass
)
{
    val widthFraction = when (windowSize) {
        WindowWidthSizeClass.Compact -> 280.dp
        WindowWidthSizeClass.Expanded -> 400.dp
        WindowWidthSizeClass.Medium -> 400.dp
        else -> 280.dp
    }

    val colors = LocalCustomColors.current
    val color = when (isMine) {
        true -> CardDefaults.cardColors(containerColor = colors.chatBubbleMine)
        else -> CardDefaults.cardColors(containerColor = colors.chatBubbleOther)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    )
    {
        Card(
            modifier = Modifier.widthIn(max = widthFraction).padding(8.dp),
            colors = color
        )
        {
            Text(
                text = message.contenido,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(8.dp),
                softWrap = true,
                overflow = TextOverflow.Clip,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit,
)
{
    TextField(
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        trailingIcon = {
            IconButton(
                onClick = onClick,
                enabled = value.isNotBlank()
            )
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send_comment),
                    tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    windowSize: WindowWidthSizeClass,
    onNavigateToOtherProfile: (UserInfo,Int) -> Unit
)
{
    val uiState by viewModel.uiState.collectAsState() // Datos
    val state by viewModel.state.collectAsState()     // Estado

    val listState = rememberLazyListState() // estado del scroll

    // detecta llegas arriba
    val scrollEnArriba by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount // total mensajes
            val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 // Último visible
            totalItems > 0 && ultimoVisible >= totalItems - 1 // detecta arriba
        }
    }

    LaunchedEffect(scrollEnArriba) {
        if (scrollEnArriba && state !is ChatState.Loading && !uiState.lastMessage) {
            viewModel.loadMessages()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        ChatTopBar(
            userName = uiState.otherUserName,
            userPhoto = uiState.otherUserPhoto,
            onNavigateToOtherProfile = { onNavigateToOtherProfile(uiState.userInfo, uiState.otherUserId) },
            windowSize = windowSize
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true
        )
        {
            items(uiState.messages) { message ->
                val isMine = message.idUsuario == uiState.userInfo.idUsuario
                MessageItem(message = message, isMine = isMine, windowSize = windowSize)
            }
        }
        ChatInput(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.currentMessage,
            onValueChange = { viewModel.onMessageChange(it) },
            onClick = { viewModel.sendMessage() }
        )
    }
}

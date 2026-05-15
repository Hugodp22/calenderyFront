package com.example.calenderyfront.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calenderyfront.Model.DataObjects.MessageResponseDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.chat.ChatState
import com.example.calenderyfront.chat.ChatViewModel
import com.example.calenderyfront.chat.Components.ChatTopBar
@Composable
fun MessageItem(
    message: MessageResponseDto,
    isMine: Boolean,
    windowSize: WindowWidthSizeClass
)
{
    val widthFraction = when (windowSize) {
        WindowWidthSizeClass.Compact -> 280.dp   // móvil
        WindowWidthSizeClass.Medium -> 400.dp    // tablet
        else -> 280.dp
    }

    val color = when (isMine) {
        true -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
        else -> CardDefaults.cardColors()
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
                modifier = Modifier.padding(8.dp),
                softWrap = true,
                overflow = TextOverflow.Clip,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
fun ChatSendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
)
{
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
    {
        Image(
            painter = painterResource(R.drawable.enviar),
            contentDescription = stringResource(R.string.send_chat),
            modifier = Modifier.size(28.dp)
        )
    }
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

    Scaffold(
        topBar = {
            ChatTopBar(uiState)
        },
        // input abajo anclado (donde escribes)
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            {
                TextField(
                    value = uiState.currentMessage,
                    onValueChange = { viewModel.onMessageChange(it) },
                    modifier = Modifier.weight(1f)
                )

                ChatSendButton(
                    enabled = uiState.currentMessage.isNotBlank(),
                    onClick = { viewModel.sendMessage() }
                )
            }
        }

    )
    { paddingValues ->

        // contenido (mensajes)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            reverseLayout = true
        )
        {
            items(uiState.messages) { message ->
                val isMine = message.idUsuario == uiState.userInfo.idUsuario
                MessageItem(message, isMine = isMine,windowSize)
            }
        }
    }
}
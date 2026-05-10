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
import com.example.calenderyfront.Model.DataObjects.Message
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.chat.ChatState
import com.example.calenderyfront.chat.ChatViewModel
import com.example.calenderyfront.chat.Components.ChatTopBar
@Composable
fun MyMessageItem(
    text: String,
    windowSize: WindowWidthSizeClass
)
{

    val widthFraction = when (windowSize) {
        WindowWidthSizeClass.Compact -> 280.dp   // móvil
        WindowWidthSizeClass.Medium -> 400.dp    // tablet
        else -> 280.dp
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            modifier = Modifier.widthIn(max = widthFraction).padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.inversePrimary
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                softWrap = true,
                overflow = TextOverflow.Clip,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
fun OtherMessageItem(
    text: String,
    windowSize: WindowWidthSizeClass
)
{

    val widthFraction = when (windowSize) {
        WindowWidthSizeClass.Compact -> 280.dp   // móvil
        WindowWidthSizeClass.Medium -> 400.dp    // tablet
        else -> 280.dp
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = widthFraction)
                .padding(8.dp),

        ) {
            Text(
                text = text,
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
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
    {
        Image(
            painter = painterResource(R.drawable.enviar),
            contentDescription = stringResource(R.string.send_chat) ,
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

    val chatLog = listOf(
        Message(idUsuario = uiState.userInfo.idUsuario, mensaje = "¡Hola! ¿Cómo vas con el proyecto?"),
        Message(idUsuario = uiState.otherUserId, mensaje = "Hola, todo bien. Justo acabo de terminar el módulo de datos."),
        Message(idUsuario = uiState.userInfo.idUsuario, mensaje = "Genial, ¿tuviste algún problema con la implementación de Kotlin?"),
        Message(idUsuario = uiState.otherUserId, mensaje = "Para nada, las data classes ahorran mucho código."),
        Message(idUsuario = uiState.userInfo.idUsuario, mensaje = "Totalmente de acuerdo. Pásame el PR cuando puedas.")
    )

    // detecta llegas arriba
    val scrollEnArriba by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount // total mensajes
            val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 // Último visible
            totalItems > 0 && ultimoVisible >= totalItems - 1 // detecta arriba
        }
    }

    // paginación al llegar arriba
//    LaunchedEffect(scrollEnArriba) {
//        if (scrollEnArriba && state !is ChatState.Loading && !uiState.lastMessage) {
//            viewModel.loadMessages() // carga más mensajes
//        }
//    }

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
            ) {

                TextField(
                    value = uiState.currentMessage,
                    onValueChange = { viewModel.onMessageChange(it) },
                    modifier = Modifier.weight(1f)
                )

                ChatSendButton(
                    text = stringResource(R.string.send_chat),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            reverseLayout = true
        )
        {
            items(chatLog) { message ->

                val isMine = message.idUsuario == uiState.userInfo.idUsuario

                if (isMine) {
                    MyMessageItem(message.mensaje, windowSize)
                }

                else {
                    OtherMessageItem(message.mensaje, windowSize)
                }
            }
        }
    }
}
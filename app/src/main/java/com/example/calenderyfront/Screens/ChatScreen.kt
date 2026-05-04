package com.example.calenderyfront.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.calenderyfront.chat.ChatState
import com.example.calenderyfront.chat.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {

    val uiState by viewModel.uiState.collectAsState() // Estado de datos
    val state by viewModel.state.collectAsState()     // Estado de control

    var text by remember { mutableStateOf("") } // Texto del input

    val currentUserId = uiState.userInfo.idUsuario // Id del usuario actual

    Column(modifier = Modifier.fillMaxSize()) {

        when (state) {

            is ChatState.Loading -> {
                // Pantalla de carga
            }

            is ChatState.Started -> {

                LazyColumn(
                    modifier = Modifier.weight(1f), // Ocupa tod0 el espacio disponible
                    reverseLayout = true            // Hace que los mensajes crezcan hacia arriba
                ) {

                    items(uiState.messages) { message ->

                        val isMine = message.idUsuario == currentUserId // Detecta si el mensaje es tuyo

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMine) {
                                Arrangement.End   // Mensaje tuyo a la derecha
                            } else {
                                Arrangement.Start // Mensaje del otro a la izquierda
                            }
                        ) {
                            Text(
                                text = message.mensaje // Contenido del mensaje
                            )
                        }
                    }
                }
            }
            is ChatState.Error -> { // Pantalla de error
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) { // Input siempre abajo

            TextField(
                value = text,
                onValueChange = { text = it },    // Actualiza el texto
                modifier = Modifier.weight(1f)    // Ocupa el máximo ancho posible
            )

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text) // Envía el mensaje
                        text = ""                   // Limpia el input
                    }
                }
            ) {
                Text("Enviar")
            }
        }
    }
}
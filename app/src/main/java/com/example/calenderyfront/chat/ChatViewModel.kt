package com.example.calenderyfront.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Chat
import com.example.calenderyfront.Model.DataObjects.Message
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class ChatViewModel(path: SavedStateHandle) : ViewModel() {

    // route, obtiene los datos enviados por navegación
    private val route = path.toRoute<Chat>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    )

    private val userInfo = route.userInfo // Usuario logeado
    private val otherUserId = route.otherUserId // Usuario con el que chateas

    // state control de flujo
    private val _state = MutableStateFlow<ChatState>(ChatState.Loading) // Estado inicial
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(ChatUiState(
        userInfo = userInfo,
            otherUserId = otherUserId,
            sendMessage = ""
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadFakeMessages() // Carga inicial de prueba
    }

    // Lista simulando backend
    private fun loadFakeMessages() {

        val fakeMessages = listOf(

            Message(
                idUsuario = userInfo.idUsuario,
                mensaje = "Mensaje mío",
                cifrado = false
            ),

            Message(
                idUsuario = otherUserId,
                mensaje = "Mensaje del otro",
                cifrado = false
            ),

            Message(
                idUsuario = otherUserId,
                mensaje = "ENC(mensaje cifrado)",
                cifrado = true
            )
        )

        // Procesa el cifrado
        val processed = fakeMessages.map {
            decryptIfNeeded(it)
        }

        _uiState.update {
            it.copy(messages = processed)
        }

        _state.value = ChatState.Started // cambia estado a listo
    }

    // enviar mensaje
    fun sendMessage(text: String) {

        val currentUiState = _uiState.value // mensajes actuales

        // mensaje nuevo del usuario
        val newMessage = Message(
            idUsuario = userInfo.idUsuario,
            mensaje = currentUiState.sendMessage,
            cifrado = false
        )

        val updatedList = listOf(newMessage) + currentUiState.messages // añade arriba el mensaje

        _uiState.update {
            it.copy(messages = updatedList)
        }

        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    // descifrido
    private fun decryptIfNeeded(message: Message): Message {

        // solo descifra si está señalado como cifrado
        return if (message.cifrado) {
            message.copy(
                mensaje = decrypt(message.mensaje)
            )
        } else {
            message
        }
    }

    private fun decrypt(text: String): String {
        return text.removePrefix("ENC(").removeSuffix(")") // simulación de descifrado
    }
}
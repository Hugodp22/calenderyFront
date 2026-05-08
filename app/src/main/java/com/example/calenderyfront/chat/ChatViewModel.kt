package com.example.calenderyfront.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Chat
import com.example.calenderyfront.Model.DataObjects.Message
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.pageSize
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
        // pruebas
        loadOtherUserInfo()
        loadFakeUser()
        loadFakeMessages()
    }

    fun loadFakeUser() {

        _state.value = ChatState.Loading

        // Simulación instantánea (sin coroutines)
        _uiState.update {
            it.copy(
                otherUserName = "David Martínez",
                otherUserPhoto = "https://i.pravatar.cc/150?img=3" // imagen de prueba
            )
        }

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
            ),

            Message(
                idUsuario = otherUserId,
                mensaje = "MENSAJE LARGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
                cifrado = false
            ),

            Message(
                idUsuario = userInfo.idUsuario,
                mensaje = "MENSAJE LARGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
                cifrado = false
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

        if (currentUiState.sendMessage.isEmpty()) {
            return
        }

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

    private var currentPageMessages = 0 // página actual
    private val currentPageSize = pageSize // tamaño dee la página

    fun loadMessages() {

        if (_state.value is ChatState.Loading) return

        _state.value = ChatState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val respuesta = RetrofitClient.chatApi.getMessages(
                    userId = userInfo.idUsuario,
                    otherUserId = otherUserId,
                    page = currentPageMessages,
                    size = currentPageSize
                )

                if (respuesta.isSuccessful) {

                    val mensajesCargados = respuesta.body()

                    if (mensajesCargados != null) {

                        val processed = mensajesCargados.content.map {
                            decryptIfNeeded(it)
                        }

                        _uiState.update {
                            it.copy(
                                messages = it.messages + processed,
                                lastMessage = mensajesCargados.content.size < currentPageSize // check last
                            )
                        }

                        _state.value = ChatState.Started

                        currentPageMessages++ // siguiente página
                    }

                } else {
                    _state.value = ChatState.Error(respuesta.code())
                }

            } catch (e: Exception) {
                _state.value = ChatState.Error(R.string.Error_Network) // err de red
            }
        }
    }

    fun onMessageChange(message: String) {
        _uiState.update {
            it.copy(currentMessage = message)
        }
    }

    fun sendMessage() {

        val messageText = _uiState.value.currentMessage

        if (messageText.isBlank()) return

        val newMessage = Message(
            idUsuario = userInfo.idUsuario,
            mensaje = messageText,
            cifrado = false
        )

        _uiState.update {
            it.copy(
                messages = listOf(newMessage) + it.messages,
                currentMessage = ""
            )
        }

        // llamada al backend
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = RetrofitClient.chatApi.sendMessage(
                    userId = userInfo.idUsuario,
                    otherUserId = otherUserId,
                    message = newMessage
                )

                if (!response.isSuccessful) {
                    // backend err
                }

            } catch (e: Exception) {

                // reintento
                try {
                    RetrofitClient.chatApi.sendMessage(
                        userId = userInfo.idUsuario,
                        otherUserId = otherUserId,
                        message = newMessage
                    )
                } catch (_: Exception) {
                    // falla con tod0
                }
            }
        }
    }

    fun loadOtherUserInfo() {

        _state.value = ChatState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = RetrofitClient.usuarioApi.obtenerVisualesDelOtroUsuario(
                    otherUserId = otherUserId
                )

                if (response.isSuccessful) {

                    val user = response.body()

                    if (user != null) {

                        _uiState.update {
                            it.copy(
                                otherUserName = user.nombreUsuario,
                                otherUserPhoto = user.fotoPerfil
                            )
                        }

                        loadMessages()
                    }

                } else {
                    _state.value = ChatState.Error(errorMessages(response.code()))
                }

            } catch (e: Exception) {
                _state.value = ChatState.Error(R.string.Error_Network)
            }
        }
    }

}
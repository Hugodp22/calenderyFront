package com.example.calenderyfront.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Chat
import com.example.calenderyfront.Model.DataObjects.Message
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.existPublicKeyInLocal
import com.example.calenderyfront.getOtherUsersPublicKeyFromLocal
import com.example.calenderyfront.getUserKeyPairFromAndroidStore
import com.example.calenderyfront.pageSize
import com.example.calenderyfront.savePublicKeyInLocal
import com.example.calenderyfront.stringToPublicKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.PrivateKey
import java.security.PublicKey
import kotlin.reflect.typeOf
import javax.crypto.Cipher
import android.util.Base64
import android.util.Log
import com.example.calenderyfront.Model.DataObjects.MessageToSend
import com.example.calenderyfront.clients.WebSocketClient

class ChatViewModel(application: Application, path: SavedStateHandle) : AndroidViewModel(application) {

    // route, obtiene los datos enviados por navegación
    private val route = path.toRoute<Chat>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    )
    private val userInfo = route.userInfo // Usuario logeado
    private val otherUserId = route.otherUserId // Usuario con el que chateas
    private val otherUserName = route.otherUserName
    private val otherUserPhoto = route.otherUserPhoto

    private var currentPageMessages = 0 // página actual
    private val currentPageSize = pageSize // tamaño dee la página

    private var myPublicKey : PublicKey? = null
    private var myPrivateKey : PrivateKey? = null
    private var otherUserPublicKey : PublicKey? = null


    // state control de flujo
    private val _state = MutableStateFlow<ChatState>(ChatState.Started) // Estado inicial
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(
        ChatUiState(
            userInfo = userInfo,
            otherUserId = otherUserId,
            idChat = 22,
            sendMessage = "",
            otherUserName = otherUserName,
            otherUserPhoto = otherUserPhoto
        )
    )

    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadPublicKeys()
    }

    fun onMessageChange(message: String) {
        _uiState.update {
            it.copy(currentMessage = message)
        }
    }

    fun loadPublicKeys() {
        val myKeys = getUserKeyPairFromAndroidStore(userId = userInfo.idUsuario)

        if (myKeys != null) {
            myPublicKey = myKeys.public
            myPrivateKey = myKeys.private
            loadOtherUserPublicKeyFromLocal()
        } else {
            _state.value = ChatState.Error(R.string.Error_PublicKey_Android)
        }
    }

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

                         val mensajesDesencriptados = mensajesCargados.content.map { message ->

                             val realMessage = myPrivateKey?.let { key ->
                                 decryptMessage(message.mensaje, key)
                             }
                             message.copy(
                                 mensaje = realMessage ?: "Error"
                             )
                         }

                        _uiState.update {
                            it.copy(
                                messages = it.messages + mensajesDesencriptados,
                                lastMessage = mensajesCargados.content.size < currentPageSize
                            )
                        }

                        _state.value = ChatState.Started
                        currentPageMessages++ // siguiente página
                    }

                } else {
                    _state.value = ChatState.Error(errorMessages(respuesta.code()))
                }

            } catch (e: Exception) {
                _state.value = ChatState.Error(R.string.Error_Network) // err de red
            }
        }
    }

    fun loadOtherUserPublicKeyFromLocal() {
        val exist = existPublicKeyInLocal(context = getApplication(), userId = otherUserId)

        if (exist) {
            val stringPublicKey = getOtherUsersPublicKeyFromLocal(
                context = getApplication(),
                userId = otherUserId
            )

            if (stringPublicKey != null) {
                otherUserPublicKey = stringToPublicKey(encodedKey = stringPublicKey)
//                loadMessages()
            }
            else {
                _state.value = ChatState.Error(R.string.Error_PublicKey_Local)
            }
        }
        else {
            requestOtherPublicKey()
        }

    }

    fun requestOtherPublicKey() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.getOtherUserPublicKey(idUsuario = otherUserId)

                if (respuesta.isSuccessful) {
                    val stringPublickKey = respuesta.body()

                    if (stringPublickKey != null) {
                        otherUserPublicKey = stringToPublicKey(stringPublickKey.publicKey)

                        savePublicKeyInLocal(
                            context = getApplication(),
                            userId = otherUserId,
                            publicKey = stringPublickKey.publicKey
                        )
//                        loadMessages()
                    }

                } else {
                    _state.value = ChatState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ChatState.Error(R.string.Error_Network)
            }
        }
    }

    private fun encryptMessage(message: String, publicKey: PublicKey): String? {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val bytesToEncrypt = message.toByteArray(Charsets.UTF_8)
            val encryptedBytes = cipher.doFinal(bytesToEncrypt)

            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        }
        catch (e: Exception) {
            Log.d("CRYPT","Error al encriptar mensaje $e")
            null
        }
    }

    private fun decryptMessage(message: String, myPrivateKey: PrivateKey): String? {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, myPrivateKey)

            val bytesToDecrypt = Base64.decode(message, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(bytesToDecrypt)

            String(decryptedBytes, Charsets.UTF_8)
        }
        catch (e: Exception) {
            Log.d("CRYPT","Error al desencriptar mensaje $e")
            null
        }
    }

    // enviar mensaje
    fun sendMessage() {
        val currentUiState = _uiState.value // mensajes actuales

        if (currentUiState.currentMessage.isEmpty() || myPublicKey == null || otherUserPublicKey == null) {
            return
        }

        val myPublickKeyMessage = encryptMessage(currentUiState.currentMessage, myPublicKey!!)
        val otherPublickKeyMessage = encryptMessage(currentUiState.currentMessage, otherUserPublicKey!!)

        if (myPublickKeyMessage.isNullOrBlank() || otherPublickKeyMessage.isNullOrBlank()) {
            return
        }

        // mensaje nuevo del usuario
        val messageToSend = MessageToSend(
            chatId = currentUiState.idChat,
            fromUser = userInfo.idUsuario,
            toUser = otherUserId,
            selfMessage = myPublickKeyMessage,
            content = otherPublickKeyMessage
        )

        val chatMessage = Message(
            idUsuario = userInfo.idUsuario,
            mensaje = currentUiState.sendMessage
        )

        val updatedList = listOf(chatMessage) + currentUiState.messages // añade arriba el mensaje

        _uiState.update {
            it.copy(
                messages = updatedList,
                sendMessage = "", //Que sea solo 1 eh, cambiarlo luego
                currentMessage = ""
            )
        }

        WebSocketClient.sendPrivateMessage(messageToSend = messageToSend)

    }
}
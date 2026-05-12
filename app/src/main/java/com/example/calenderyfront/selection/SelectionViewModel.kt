package com.example.calenderyfront.selection

import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Selection
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.getUserKeyPairFromAndroidStore
import com.example.calenderyfront.pageSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.PrivateKey
import javax.crypto.Cipher
import kotlin.reflect.typeOf

class SelectionViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val chatOption = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).chatOption

    private var myPrivateKey : PrivateKey? = null

    private val _uiState = MutableStateFlow(SelectionUiState(userInfo, chatOption))
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<SelectionState>(SelectionState.Iniciado)
    val state: StateFlow<SelectionState> = _state.asStateFlow()

    private var currentPageSelection = 0
    private val currentPageSize = pageSize

    init {
        loadNextPage()
    }

    fun onSearchChange(searchName: String) {
        _uiState.update { it.copy(
            searchName = searchName,
            selectionUsersChatList = emptyList(),
            selectionUsersProfileList = emptyList(),
            lastPage = false
        )}
        currentPageSelection = 0
    }

    fun loadMyPrivateKey() {
        val myKeys = getUserKeyPairFromAndroidStore(userId = userInfo.idUsuario)

        if (myKeys != null) {
            myPrivateKey = myKeys.private
        }

        else {
            _state.value = SelectionState.Error(R.string.Error_PublicKey_Local)
        }
    }

    private fun decryptMessage(encryptedMessage: String, myPrivateKey: PrivateKey): String {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, myPrivateKey)

            val bytesToDecrypt = Base64.decode(encryptedMessage, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(bytesToDecrypt)

            String(decryptedBytes, Charsets.UTF_8)
        }
        catch (e: Exception) {
            Log.d("CRYPT","Error al desencriptar mensaje $e")
            return ""
        }
    }

    fun searchProfileByName() {
        val currentState = _uiState.value

        if (_state.value is SelectionState.Cargando || currentState.lastPage) {
            return
        }

        _state.value = SelectionState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.obtenerPerfilUsuariosBuscados(
                    nombre = currentState.searchName,
                    page = currentPageSelection
                )

                if (respuesta.isSuccessful) {
                    val usuariosCargados = respuesta.body()

                    if (usuariosCargados != null) {
                        _uiState.update { it.copy(
                            selectionUsersProfileList = it.selectionUsersProfileList + usuariosCargados.content,
                            lastPage = usuariosCargados.content.size < currentPageSize
                        )}

                        _state.value = SelectionState.PaginaCargada
                        currentPageSelection++
                    }
                }
                else {
                    _state.value = SelectionState.Error(errorMessages(respuesta.code()))
                }

            }
            catch (e: Exception) {
                _state.value = SelectionState.Error(R.string.Error_Network)
            }
        }
    }

    fun searchContactByName() {
        val currentState = _uiState.value

        if (_state.value is SelectionState.Cargando || currentState.lastPage) {
            return
        }

        _state.value = SelectionState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.obtenerContactosBuscados(
                    nombre = currentState.searchName,
                    page = currentPageSelection
                )

                if (respuesta.isSuccessful) {
                    val contactosCargados = respuesta.body()
                    if (contactosCargados != null) {

                        val mensajesDesencriptados = contactosCargados.content.map { contact ->

                            val realMessage = myPrivateKey?.let { key ->
                                decryptMessage(contact.ultimoMensaje, key)
                            }

                            Log.d("chatId"," Es = ${contact.idChat}")

                            contact.copy(
                                ultimoMensaje = realMessage ?: "Error"
                        )}

                        _uiState.update { it.copy(
                            selectionUsersChatList = it.selectionUsersChatList + mensajesDesencriptados,
                            lastPage = contactosCargados.content.size < currentPageSize
                        )}
                        _state.value = SelectionState.PaginaCargada
                        currentPageSelection++
                    }
                }
                else {
                    _state.value = SelectionState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = SelectionState.Error(R.string.Error_Network)
            }
        }
    }

    fun loadNextPage() {
        loadMyPrivateKey()
        if (chatOption) {
            searchContactByName()
        }
        else {
            searchProfileByName()
        }
    }
}
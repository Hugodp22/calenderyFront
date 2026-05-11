package com.example.calenderyfront.waitingForLink

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.clients.WebSocketClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.userSecurityKeyCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class WaitingForLinkViewModel(application: Application,path: SavedStateHandle): AndroidViewModel(application) {

    val userInfo: UserInfo = path.toRoute<VerifyLink>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _state = MutableStateFlow<WaitingForLinkState>(WaitingForLinkState.Iniciado)
    val state: StateFlow<WaitingForLinkState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(WaitingForLinkUiState(userInfo))
    val uiState: StateFlow<WaitingForLinkUiState> = _uiState.asStateFlow()

    /**
     * Funcion para pedir en bucle una peticion para revisar si ya estas registrado
     */
    fun checkValidation() {
        viewModelScope.launch(Dispatchers.IO) {
            var invalid = true
            val currentUiState = _uiState.value
            val idUsuario = currentUiState.userInfo.idUsuario

            while (invalid) {
                try {
                    val respuesta = RetrofitClient.usuarioApi.validarUsuario(idUsuario)

                    if (respuesta.isSuccessful) {
                        invalid = false
                        _state.value = WaitingForLinkState.Cargando
                        sendPublicKey()
                    }

                    else {
                        Log.d("WaitingValidation","Error al validar ${respuesta.code()}")
                        delay(3000)
                    }
                }
                catch (e: Exception) {
                    Log.d("WaitingValidation","Error $e")
                    delay(3000)
                }
            }
        }
    }

    /**
     * Funcion para generar una clave publica y privada a nivel interno, y mandar la publica
     * al back en formato Base64
     */
    fun sendPublicKey() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val publicKey = userSecurityKeyCreation(userInfo.idUsuario)
                val publicKeyDto = PublicKeyDto(publicKey) //Lo envolvemos para que el back no tenga problemas
                val respuesta = RetrofitClient.usuarioApi.mandarClavePublica(userInfo.idUsuario, publicKeyDto)

                if (respuesta.isSuccessful) {
                    WebSocketClient.connect(getApplication())
                    WebSocketClient.userValidation()
                    _state.value = WaitingForLinkState.Exito(userInfo)
                }

                else {
                    Log.d("WaitingValidation","Error al mandar la PK ${respuesta.code()} ${respuesta.message()}")
                }
            }
            catch (e: Exception) {
                Log.d("WaitingValidation","Error desconocido PK $e")
            }
        }
    }

    fun resendEmail() {
        _state.value = WaitingForLinkState.Cargando
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.reenviarCorreo(userInfo.idUsuario)

                if (respuesta.isSuccessful) {
                    _state.value = WaitingForLinkState.Iniciado
                }

                else {
                    _state.value = WaitingForLinkState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = WaitingForLinkState.Error(R.string.Error_resend)
            }
        }
    }
}
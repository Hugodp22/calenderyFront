package com.example.calenderyfront.waitingForLink

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.RetrofitClient
import com.example.calenderyfront.securityKeyCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class WaitingForLinkViewModel(path: SavedStateHandle): ViewModel() {

    val userInfo: UserInfo = path.toRoute<VerifyLink>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _state = MutableStateFlow<WaitingForLinkState>(WaitingForLinkState.Iniciado)
    val state: StateFlow<WaitingForLinkState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(WaitingForLinkUiState(userInfo))
    val uiState: StateFlow<WaitingForLinkUiState> = _uiState.asStateFlow()

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

    fun sendPublicKey() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val publicKey = securityKeyCreation()
                val publicKeyDto = PublicKeyDto(publicKey)
                val respuesta = RetrofitClient.usuarioApi.mandarClavePublica(userInfo.idUsuario, publicKeyDto)

                if (respuesta.isSuccessful) {
                    _state.value = WaitingForLinkState.Exito(userInfo)
                }

                else {
                    Log.d("WaitingValidation","Error al mandar la PK ${respuesta.code()}")
                }
            }
            catch (e: Exception) {
                Log.d("WaitingValidation","Error desconocido PK $e")
            }
        }
    }
}
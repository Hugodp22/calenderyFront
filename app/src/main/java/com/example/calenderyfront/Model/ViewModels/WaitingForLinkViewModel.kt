package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.Model.DataObjects.VerifyLink
import com.example.calenderyfront.Model.States.WaitingForLinkState
import com.example.calenderyfront.Model.UiStates.WaitingForLinkUiState
import com.example.calenderyfront.RetrofitClient
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
                        _state.value = WaitingForLinkState.Exito(currentUiState.userInfo)
                    }

                    else {
                        delay(3000)
                    }
                }
                catch (e: Exception) {
                    delay(3000)
                }
            }
        }
    }
}
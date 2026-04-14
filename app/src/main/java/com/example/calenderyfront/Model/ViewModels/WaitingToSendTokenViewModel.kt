package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.States.WaitingToSendTokenState
import com.example.calenderyfront.Model.UiStates.WaitingToSendTokenUiState
import com.example.calenderyfront.R
import com.example.calenderyfront.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.securityKeyCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaitingToSendTokenViewModel: ViewModel() {
    private val _state = MutableStateFlow<WaitingToSendTokenState>(WaitingToSendTokenState.Iniciado)
    val state: StateFlow<WaitingToSendTokenState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(WaitingToSendTokenUiState(""))
    val uiState : StateFlow<WaitingToSendTokenUiState> = _uiState.asStateFlow()

    //fun onToken(token: String) {
    //    _uiState.update { it.copy(token = token) }
    //    _state.value = WaitingToSendTokenState.Cargando
    //    tokenValidation()
    //}
//
    //fun tokenValidation() {
    //    val currentUiState = _uiState.value
    //    viewModelScope.launch(Dispatchers.IO) {
    //        try {
    //            val respuesta = RetrofitClient.usuarioApi.validarToken(currentUiState.token)
//
    //            if (respuesta.isSuccessful) {
    //                val userInfo = respuesta.body()
//
    //                if (userInfo != null) {
    //                    sendPublicKey(userInfo)
    //                }
    //            }
//
    //            else {
    //                val codigoError = respuesta.code()
    //                _state.value = WaitingToSendTokenState.Error(errorMessages(codigoError))
    //            }
    //        }
    //        catch (e: Exception) {
    //            _state.value = WaitingToSendTokenState.Error(R.string.Error_Network)
    //        }
    //    }
    //}
//
    //fun sendPublicKey(userInfo: UserInfo) {
    //    viewModelScope.launch(Dispatchers.IO) {
//
    //        try {
    //            val publicKey = securityKeyCreation()
    //            val respuesta = RetrofitClient.usuarioApi.mandarClavePublica(userInfo.id, publicKey)
//
    //            if (respuesta.isSuccessful) {
    //                val resultado = respuesta.body()
//
    //                if (resultado != null) {
    //                    _state.value = WaitingToSendTokenState.Exito(userInfo)
    //                }
//
    //            }
    //            else {
    //                val codigoError = respuesta.code()
    //                _state.value = WaitingToSendTokenState.Error(codigoError)
    //            }
    //        }
    //        catch (e: Exception) {
//
    //        }
    //    }
    //}
}
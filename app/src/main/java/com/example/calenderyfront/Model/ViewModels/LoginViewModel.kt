package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.userAuth.SessionManager
import com.example.calenderyfront.Model.DataObjects.UserLogin
import com.example.calenderyfront.Model.States.LoginState
import com.example.calenderyfront.Model.UiStates.LoginUiState
import com.example.calenderyfront.R
import com.example.calenderyfront.RetrofitClient
import com.example.calenderyfront.errorMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<LoginState>(LoginState.Iniciado)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState("","",""))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _errorEmail = MutableStateFlow(false)
    val errorEmail : StateFlow<Boolean> = _errorEmail.asStateFlow()

    private val _errorKeypass = MutableStateFlow(false)
    val errorKeypass: StateFlow<Boolean> = _errorKeypass.asStateFlow()

    fun onEmailChange(nuevoCorreo: String) {
        _errorEmail.value = false
        _uiState.update { it.copy(email = nuevoCorreo) }
    }

    fun onKeypassChange(nuevaPass: String) {
        _errorKeypass.value = false
        _uiState.update { it.copy(keypass = nuevaPass) }
    }

    fun tryLogin() {
        val currentUiState = _uiState.value

        if (_state.value is LoginState.Error) {
            _state.value = LoginState.Iniciado
        }

        if (currentUiState.email == "" || currentUiState.email.isEmpty() || !currentUiState.email.contains("@") || !currentUiState.email.contains(".")) {
            _errorEmail.value = true
            _state.value = LoginState.Error(R.string.Error_email_message)
            return
        }

        _errorEmail.value = false

        if (currentUiState.keypass.isEmpty()) {
            _errorKeypass.value = true
            _state.value = LoginState.Error(R.string.Error_pass_message)
            return
        }
        _errorKeypass.value = false

        //Si esta todo bien, ponemos a cargar mientras hacemos la peticion
        _state.value = LoginState.Cargando

        searchUser(currentUiState)
    }

    fun searchUser(currentUiState: LoginUiState) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarioBuscar = UserLogin(
                    email = currentUiState.email,
                    keypass = currentUiState.keypass
                )

                val respuesta = RetrofitClient.usuarioApi.buscarPerfilUsuarioPorLog(usuarioBuscar)

                if (respuesta.isSuccessful) {
                    val userInfo = respuesta.body()

                    if (userInfo != null) {
                        //Se guarda a nivel interno el email y la contraseña
                        SessionManager.saveSession(getApplication(),currentUiState.email, currentUiState.keypass)
                        _state.value = LoginState.Exito(userInfo)
                    }

                    else {
                        SessionManager.clearSession(getApplication())
                        _state.value = LoginState.Error(R.string.Error_Profile_Message)
                    }
                }
                else {
                    val codigoError = respuesta.code()
                    _state.value = LoginState.Error(errorMessages(codigoError))
                }

            } catch(e: Exception) {
                //Error de red
                _state.value = LoginState.Error(R.string.Error_Network)
            }
        }
    }

}

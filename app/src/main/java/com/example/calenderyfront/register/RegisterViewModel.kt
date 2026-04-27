package com.example.calenderyfront.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.userAuth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Iniciado)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(RegisterUiState("", "", "", ""))
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _errorName = MutableStateFlow(false)
    val errorName: StateFlow<Boolean> = _errorName.asStateFlow()

    private val _errorEmail = MutableStateFlow(false)
    val errorEmail : StateFlow<Boolean> = _errorEmail.asStateFlow()

    private val _errorKeypass = MutableStateFlow(false)
    val errorKeypass: StateFlow<Boolean> = _errorKeypass.asStateFlow()

    private val _errorMesagge = MutableStateFlow("")
    val errorMessage : StateFlow<String> = _errorMesagge.asStateFlow()

    fun onNameChange(nuevoNombre: String) {
        //Para que en caso de estar rojo el input por un error se reinicie el color del input al volver a escribir
        _errorName.value = false
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onEmailChange(nuevoCorreo: String) {
        _errorEmail.value = false
        _uiState.update { it.copy(email = nuevoCorreo) }
    }

    fun onKeypassChange(nuevaPass: String) {
        _errorKeypass.value = false
        _uiState.update { it.copy(keypass = nuevaPass) }
    }

    fun onConfirmKeypassChange(nuevaPassConfirm: String) {
        _errorKeypass.value = false
        _uiState.update { it.copy(keypassConfirm = nuevaPassConfirm) }
    }

    /**
     * Funcion para validar el contenido del usuario antes del registro
     */
    fun tryRegister() {
        val currentUiState = _uiState.value

        //Quitamos el error mientras validamos
        if (_state.value is RegisterState.Error) {
            _state.value = RegisterState.Iniciado
        }

        if (currentUiState.nombre == "" || currentUiState.nombre.isEmpty()) {
            _errorName.value = true
            _state.value = RegisterState.Error(R.string.Error_name_message)
            return
        }

        _errorName.value = false

        if (currentUiState.email.isEmpty() || !currentUiState.email.contains("@") || !currentUiState.email.contains(".")) {
            _errorEmail.value = true
            _state.value = RegisterState.Error(R.string.Error_email_message)
            return
        }
        _errorEmail.value = false

        if (currentUiState.keypass != currentUiState.keypassConfirm || currentUiState.keypass.isEmpty() || currentUiState.keypassConfirm.isEmpty()) {
            _errorKeypass.value = true
            _state.value = RegisterState.Error(R.string.Error_pass_message)
            return
        }

        _errorKeypass.value = false

        //Si esta todo bien, ponemos a cargar mientras hacemos la peticion
        _state.value = RegisterState.Cargando

        saveUser()
    }

    /**
     * Funcion para guardar el usuario en la BD
     */
    fun saveUser() {
        val currentUiState = _uiState.value
        viewModelScope.launch {
            try {
                val usuarioEnviar = UserRegister(
                    nombre = currentUiState.nombre,
                    email = currentUiState.email,
                    keypass = currentUiState.keypass,
                )

                SessionManager.clearSession(getApplication())
                val respuesta = RetrofitClient.usuarioApi.registrarUsuario(usuarioEnviar)

                if (respuesta.isSuccessful) {
                    val userInfo = respuesta.body()

                    if (userInfo != null) {
                        //Se guarda la cabecera para las siguientes llamadas
                        SessionManager.saveSession(getApplication(),currentUiState.email, currentUiState.keypass)
                        _state.value = RegisterState.Exito(userInfo)
                    }

                    else {
                        val codigoError = respuesta.code()
                        _state.value = RegisterState.Error(errorMessages(codigoError))
                    }
                }

                else {
                    val codigoError = respuesta.code()
                    _state.value = RegisterState.Error(errorMessages(codigoError))
                }
            } catch (e: Exception) {
                // Error de red
                _state.value = RegisterState.Error(R.string.Error_Network)
            }
        }

    }
}
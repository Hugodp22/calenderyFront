package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserLog
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.UiStates.RegisterUiState
import com.example.calenderyfront.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Iniciado)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(RegisterUiState("", "", "", ""))
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _errorKeypass = MutableStateFlow(false)
    val errorKeypass: StateFlow<Boolean> = _errorKeypass.asStateFlow()

    private val _errorMesagge = MutableStateFlow("")
    val errorMessage : StateFlow<String> = _errorMesagge.asStateFlow()

    init {

    }

    fun onNameChange(nuevoNombre: String) {
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onEmailChange(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo) }
    }

    fun onKeypassChange(nuevaPass: String) {
        //Para que en caso de estar rojo el input por un error se reinicie el color del input al volver a escribir
        _errorKeypass.value = false
        _uiState.update { it.copy(keypass = nuevaPass) }
    }

    fun onConfirmKeypassChange(nuevaPassConfirm: String) {
        _errorKeypass.value = false
        _uiState.update { it.copy(keypassConfirm = nuevaPassConfirm) }
    }

    fun tryLogin() {
        val currentUiState = _uiState.value

        if (_state.value is RegisterState.Error) {
            _state.value = RegisterState.Iniciado
        }

        //Si las contraseñas no son las mismas, o directamente alguna esta vacia, da error.
        if (currentUiState.keypass != currentUiState.keypassConfirm || currentUiState.keypass.isEmpty() || currentUiState.keypassConfirm.isEmpty()) {
            _errorKeypass.value = true
            _state.value = RegisterState.Error("Las contraseñas deben coincidir")
            return
        }

        //Si esta todo bien, ponemos a cargar mientras hacemos la peticion
        _errorKeypass.value = false
        _state.value = RegisterState.Cargando

        saveUser(currentUiState)
    }

    fun saveUser(currentUiState: RegisterUiState) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarioEnviar = UserLog(
                    nombre = currentUiState.nombre,
                    email = currentUiState.correo,
                    keypass = currentUiState.keypass
                )

                val respuesta = RetrofitClient.usuarioApi.registrarUsuario(usuarioEnviar)

                if (respuesta.isSuccessful) {
                    val user = respuesta.body()

                    //if (user != null) {
                    //    _state.value = LoginState.Exito(user)
                    //}

                    //else {
                    //    _state.value = LoginState.Error("El servidor envió datos vacíos")
                    //}
                    _state.value = RegisterState.Iniciado
                    //_state.value = RegisterState.Exito() Aqui recibiriamos el usuario

                }

                else {
                    val codigoError = respuesta.code()
                    val mensajeError = when (codigoError) {
                        404 -> "Servidor no encontrado (404)"
                        500 -> "Error interno del servidor (500)"
                        else -> "Error desconocido: $codigoError"
                    }
                    _state.value = RegisterState.Error(mensajeError)
                }
            } catch (e: Exception) {
                // Error de red
                _state.value = RegisterState.Error("Error de red: ${e.localizedMessage}")
            }
        }

    }
}
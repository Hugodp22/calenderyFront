package com.example.calenderyfront.Model.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.UserRegister
import com.example.calenderyfront.Model.States.RegisterState
import com.example.calenderyfront.Model.UiStates.RegisterUiState
import com.example.calenderyfront.R
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

    private val _errorName = MutableStateFlow(false)
    val errorName: StateFlow<Boolean> = _errorName.asStateFlow()

    private val _errorEmail = MutableStateFlow(false)
    val errorEmail : StateFlow<Boolean> = _errorEmail.asStateFlow()

    private val _errorKeypass = MutableStateFlow(false)
    val errorKeypass: StateFlow<Boolean> = _errorKeypass.asStateFlow()

    private val _errorMesagge = MutableStateFlow("")
    val errorMessage : StateFlow<String> = _errorMesagge.asStateFlow()

    init {

    }

    fun onNameChange(nuevoNombre: String) {
        _errorName.value = false
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onEmailChange(nuevoCorreo: String) {
        _errorEmail.value = false
        _uiState.update { it.copy(email = nuevoCorreo) }
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

    fun tryRegister() {
        val currentUiState = _uiState.value

        if (_state.value is RegisterState.Error) {
            _state.value = RegisterState.Iniciado
        }

        if (currentUiState.nombre == "" || currentUiState.nombre.isEmpty()) {
            _errorName.value = true
            _state.value = RegisterState.Error(R.string.Error_name_message)
            return
        }

        _errorName.value = false

        if (currentUiState.email == "" || currentUiState.email.isEmpty() || !currentUiState.email.contains("@") || !currentUiState.email.contains(".")) {
            _errorEmail.value = true
            _state.value = RegisterState.Error(R.string.Error_email_message)
            return
        }
        _errorEmail.value = false

        //Si las contraseñas no son las mismas, o directamente alguna esta vacia, da error.
        if (currentUiState.keypass != currentUiState.keypassConfirm || currentUiState.keypass.isEmpty() || currentUiState.keypassConfirm.isEmpty()) {
            _errorKeypass.value = true
            _state.value = RegisterState.Error(R.string.Error_pass_message)
            return
        }
        _errorKeypass.value = false

        //Si esta todo bien, ponemos a cargar mientras hacemos la peticion
        _state.value = RegisterState.Cargando

        saveUser(currentUiState)
    }

    fun saveUser(currentUiState: RegisterUiState) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarioEnviar = UserRegister(
                    nombre = currentUiState.nombre,
                    email = currentUiState.email,
                    keypass = currentUiState.keypass
                )

                val respuesta = RetrofitClient.usuarioApi.registrarUsuario(usuarioEnviar)

                if (respuesta.isSuccessful) {
                    _state.value = RegisterState.Iniciado
                    val user = respuesta.body()

                    //if (user != null) {
                    //    _state.value = LoginState.Exito(user)
                    //}

                    //else {
                    //    _state.value = LoginState.Error("El servidor envió datos vacíos")
                    //}
                    //_state.value = RegisterState.Exito() Aqui recibiriamos el usuario

                }

                else {
                    val codigoError = respuesta.code()
                    val mensajeError = when (codigoError) {
                        404 -> R.string.Error_404_Message
                        500 -> R.string.Error_500_message
                        else -> R.string.Error_Unknow_Message
                    }
                    _state.value = RegisterState.Error(mensajeError)
                }
            } catch (e: Exception) {
                // Error de red
                _state.value = RegisterState.Error(R.string.Error_Network)
            }
        }

    }
}
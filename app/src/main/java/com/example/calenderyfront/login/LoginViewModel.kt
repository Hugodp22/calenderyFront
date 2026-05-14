package com.example.calenderyfront.login

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.connectWebSocket
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.getUserKeyPairFromAndroidStore
import com.example.calenderyfront.userAuth.SessionManager
import com.example.calenderyfront.userSecurityKeyCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<LoginState>(LoginState.Iniciado)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState("", "", ""))
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

    /**
     * Funcion para validar el email y la contraseña
     */
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
                SessionManager.saveSession(getApplication(),currentUiState.email, currentUiState.keypass)

                val respuesta = RetrofitClient.usuarioApi.buscarPerfilUsuarioPorCabecera()

                if (respuesta.isSuccessful) {
                    val userInfo = respuesta.body()

                    if (userInfo != null) {
                        val keyPair = getUserKeyPairFromAndroidStore(userId = userInfo.idUsuario)

                        if (keyPair != null) {
                            val publicKeyString = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
                            checkPublicKey(userInfo = userInfo, publicKey = publicKeyString)
                        }
                        else {
                            sendPublicKey(userInfo)
                        }
                    }

                    else {
                        SessionManager.clearSession(getApplication())
                        _state.value = LoginState.Error(R.string.Error_Profile_Message)
                    }
                }
                else {
                    SessionManager.clearSession(getApplication())
                    val codigoError = respuesta.code()
                    _state.value = LoginState.Error(errorMessages(codigoError))
                }

            }
            catch(e: Exception) {
                //Error de red
                _state.value = LoginState.Error(R.string.Error_Network)
            }
        }
    }

    fun checkPublicKey(userInfo: UserInfo, publicKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.comprobarClavePublica(idUsuario = userInfo.idUsuario, clavePublica = publicKey)

                if (respuesta.isSuccessful) {
                    connectWebSocket(context = getApplication())
                    _state.value = LoginState.Exito(userInfo)
                }

                else {
                    sendPublicKey(userInfo)
                }
            }
            catch (e: Exception) {
                _state.value = LoginState.Error(R.string.Error_Network)
            }
        }
    }

    /**
     * Funcion para generar una clave publica y privada a nivel interno, y mandar la publica
     * al back en formato Base64
     */
    fun sendPublicKey(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val publicKey = userSecurityKeyCreation(userInfo.idUsuario)
                val publicKeyDto = PublicKeyDto(publicKey)
                val respuesta = RetrofitClient.usuarioApi.mandarClavePublica(userInfo.idUsuario, publicKeyDto)

                if (respuesta.isSuccessful) {
                    connectWebSocket(context = getApplication())
                    _state.value = LoginState.Exito(userInfo)
                }

                else {
                    _state.value = LoginState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = LoginState.Error(errorMessages(R.string.Error_Network))
            }
        }
    }

}
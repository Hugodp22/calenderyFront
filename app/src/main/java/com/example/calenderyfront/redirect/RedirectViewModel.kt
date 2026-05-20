package com.example.calenderyfront.redirect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserValidation
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.connectWebSocket
import com.example.calenderyfront.getUserKeyPairFromAndroidStore
import com.example.calenderyfront.userAuth.SessionManager
import com.example.calenderyfront.userSecurityKeyCreation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RedirectViewModel(application: Application) : AndroidViewModel(application){

    private val _state = MutableStateFlow<RedirectState>(RedirectState.Cargando)
    val state: StateFlow<RedirectState> = _state.asStateFlow()

    init {
        isLogin()
    }

    fun isLogin() {
        if (SessionManager.isUserLoggedIn(getApplication())) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val email = SessionManager.getEmail(getApplication())
                    if (email != null) {
                        val respuesta = RetrofitClient.usuarioApi.validarUsuarioPorCorreo(email)

                        if (respuesta.isSuccessful) {
                            val userValidation = respuesta.body()

                            if (userValidation != null) {
                                isValidate(userValidate = userValidation)
                            }
                            else {
                                _state.value = RedirectState.NoLogin
                            }
                        }
                    }
                    else {
                        _state.value = RedirectState.NoLogin
                    }
                }
                catch (e: Exception) {
                    _state.value = RedirectState.NoLogin
                }
            }
        }
        else {
            _state.value = RedirectState.NoLogin
        }
    }

    fun isValidate(userValidate: UserValidation) {
        if (userValidate.enable) {

            if (getUserKeyPairFromAndroidStore(userId = userValidate.userInfo.idUsuario) != null) {
                connectWebSocket(context = getApplication())
                _state.value = RedirectState.Exito(userValidate.userInfo)
            }
            else {
                sendPublicKey(userValidate.userInfo)
            }
        }
        else {
            _state.value = RedirectState.NoValidate(userValidate.userInfo)
        }
    }

    fun sendPublicKey(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val publicKey = userSecurityKeyCreation(userInfo.idUsuario)
                val publicKeyDto = PublicKeyDto(publicKey)
                val respuesta = RetrofitClient.usuarioApi.mandarClavePublica(userInfo.idUsuario, publicKeyDto)

                if (respuesta.isSuccessful) {
                    connectWebSocket(context = getApplication())
                    _state.value = RedirectState.Exito(userInfo)
                }

                else {
                    _state.value = RedirectState.NoLogin
                }
            }
            catch (e: Exception) {
                _state.value = RedirectState.NoLogin
            }
        }
    }
}

package com.example.calenderyfront.redirect

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calenderyfront.Model.DataObjects.PublicKeyDto
import com.example.calenderyfront.Model.DataObjects.UserInfo
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
                    //Si esta aqui, es que tiene cabecera, entonces la cuenta existe, pero no sabemos
                    //si esta validada o no, asi que la validamos.
                    val email = SessionManager.getEmail(getApplication())?.trim()

                    if (email != null) {
                        val respuesta = RetrofitClient.usuarioApi.validarUsuarioPorCorreo(email)

                        if (respuesta.isSuccessful) {
                            val userValidation = respuesta.body()

                            if (userValidation != null) {

                                if (userValidation.enable) {
                                    val keyPair = getUserKeyPairFromAndroidStore(userId = userValidation.userInfo.idUsuario)

                                    if (keyPair != null) {
                                        val publicKeyString = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
                                        checkPublicKey(userInfo = userValidation.userInfo, publicKey = publicKeyString)
                                    }

                                    else {
                                        sendPublicKey(userInfo = userValidation.userInfo)
                                    }
                                }
                                else {
                                    _state.value = RedirectState.NoValidate(userValidation.userInfo)
                                }
                            }
                        }
                        else {
                            SessionManager.clearSession(getApplication())
                            _state.value = RedirectState.NoLogin
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

    fun checkPublicKey(userInfo: UserInfo, publicKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.comprobarClavePublica(idUsuario = userInfo.idUsuario, clavePublica = publicKey)

                if (respuesta.isSuccessful) {
                    connectWebSocket(context = getApplication())
                    _state.value = RedirectState.Exito(userInfo)
                }

                else {
                    sendPublicKey(userInfo)
                }
            }
            catch (e: Exception) {
                _state.value = RedirectState.NoLogin
            }
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